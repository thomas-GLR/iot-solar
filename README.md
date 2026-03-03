# API communiquant avec l'applciation IOT solar et controllant l'ESP32

## Création de la BDD
``` postgresql
> psql -U postgres
create role admin superuser login;
create role iotsolar login;
alter role iotsolar with password iotsolar
create role read_only login;
create role read_write login;
create database {iotsolar owner iotsolar;
\c iotsolar;
drop schema public;
create schema public authorization iotsolar;
exit
```

## Script pour supprimer la BDD
``` postgresql
drop table if exists esp_parameter cascade;
drop table if exists users cascade;
drop table if exists temperatures cascade;
drop table if exists reading_device cascade;
drop table if exists resistance_state cascade;

DROP SEQUENCE if exists esp_parameter_id_seq;
DROP SEQUENCE if exists users_id_seq;
DROP SEQUENCE if exists temperatures_id_seq;
DROP SEQUENCE if exists reading_device_id_seq;
DROP SEQUENCE if exists resistance_state_id_seq;

delete from flyway_schema_history;
```

## Mettre en place mqtt avec mosquitto et docker pour le local

### Certifcat pour SSL
Générer le fichier ca.key
``` bash
openssl genrsa -out ca.key 2048
```
Générer le fichier ca.crt à partir de la clé
``` bash
openssl req -new -x509 -days 365 -key ca.key -out ca.crt -subj "/C=FR/ST=Rhone/L=Lyon/O=MyOrg/CN=MyCA"
```
Générer la clé du serveur
``` bash
openssl genrsa -out server.key 2048
```
Dans subjectAltName il faut mettre l'IP ou DNS du serveur mosquitto et générer la demande de signature de certificat (CSR) pour le serveur :
``` bash
openssl req -new -out server.csr -key server.key -subj "/C=FR/ST=Rhone/L=Lyon/O=MyOrg/CN=localhost" -addext "subjectAltName=IP:192.168.1.100,DNS:mosquitto.local"
```
Dans mon cas ça n'a pas fonctionné (windows) il faut passer par un fichier d'extension :
Crée un fichier ext.cnf dans le même dossier que les certificats :
```
subjectAltName=IP:192.168.1.100,DNS:mosquitto.local
```
Et lancer la commande :
``` bash
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 365 -extfile ext.cnf
```
Signer le certificat du serveur avec la CA pour générer le certificat final du serveur :
``` bash
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt -days 365
```
Déplacer tous les fichiers générés dans un dossier mosquitto/config

### Ajouter le certificat à java
Se mettre au niveau de mosquitto/config et exécuter la commande suivante pour ajouter le certificat à java :
``` bash
keytool -import -alias mosquittoCA -file ca.crt -keystore "C:\Program Files\Java\jdk-21\lib\security\cacerts" -storepass changeit
```
Pour lister tous les certificats existants dans java :
``` bash
keytool -list -keystore "C:\Program Files\Java\jdk-21\lib\security\cacerts" -storepass changeit
```
Pour supprimer un certificat précédent
``` bash
keytool -delete -alias mosquittoCA -keystore "C:\Program Files\Java\jdk-21\lib\security\cacerts" -storepass changeit
```

### Mosquitto.conf
```
listener 8883
cafile /mosquitto/config/ca.crt
certfile /mosquitto/config/server.crt
keyfile /mosquitto/config/server.key

require_certificate false
allow_anonymous false
password_file /mosquitto/config/passwordfile
```

### Lancer le docker
Attention : il faut créer le fichier passwordfile avant de lancer le docker sinon mosquitto ne démarrera pas, le laisser vierge, on rajoutera les utilisateurs après

``` bash
docker run --name mosquitto -it -p 8883:8883 -v \chemin\vers\mosquitto\config:/mosquitto/config eclipse-mosquitto
```

### Ajouter un utilisateur
``` bash
docker exec -it <container_id> mosquitto_passwd -b /mosquitto/config/passwordfile <user> <password>
```
## Déploiement en container
Il faut ajouter le fichier ca.crt dans le dossier certs qui est à la racine du projet pour que l'application puisse communiquer avec mosquitto en SSL
```
solar-iot/
├── certs/
|   └── ca.crt
├── src/
│   └── ...
├── docker-compose.yml
├── Dockerfile
├── .env
└── entrypoint.sh
```
Créer le fichier .env à la racine du projet avec les variables d'environnement nécessaires à l'application
```
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=

MQTT_HOST=
MQTT_PORT=
MQTT_USER=
MQTT_PASSWORD=

JWT_SECRET=
```
Le fichier entrypoint.sh est utilisé pour ajouter le ca.crt dans le keystore de java au démarrage du container pour que l'application puisse communiquer avec mosquitto en SSL


Lancer la commande suivante pour builder l'image et la lancer en local
``` bash
docker compose up -d
```

## Mettre l'application en prod sur un raspberry pi
### La première fois

Se connecter au repo github (stockage de l'image)
``` bash
echo "VOTRE_TOKEN" | docker login ghcr.io -u VOTRE_USERNAME --password-stdin
```
Builder et push l'image sur le repo. Attention le username doit être en minuscule
``` bash
docker buildx build --platform linux/arm64 -t ghcr.io/VOTRE_USERNAME/iot-solar-api:latest --push .
```
Si c'est la première fois que buildx est utilisé il faut lancer la commande suivante
``` bash
docker buildx create --use
```
Transférer les fichiers de config sur le raspberry pi
``` bash
scp docker-compose.yml pi@<IP_RASPBERRY>:/home/pi/iot-solar/
scp .env pi@<IP_RASPBERRY>:/home/pi/iot-solar/
# si besoin de transférer les certificats pour mqtt (pour ma part il se trouve déjà sur le raspberry pi)
scp -r certs/ pi@<IP_RASPBERRY>:/home/pi/iot-solar/certs/
```
Se connecter au raspberry pi et s'authentifier sur le repo github pour pouvoir pull l'image. Attention le username doit être en minuscule
``` bash
ssh pi@<IP_RASPBERRY>
echo "VOTRE_TOKEN" | docker login ghcr.io -u VOTRE_USERNAME --password-stdin
```
Lancer l'application
``` bash
cd iot-solar
docker-compose up -d
```
### Mettre à jour sur le raspberry pi
Mettre à jour l'image sur le repo github
``` bash
docker buildx build --platform linux/arm64 -t ghcr.io/VOTRE_USERNAME/iot-solar-api:latest --push .
```
Sur le raspberry pi, pull la nouvelle image (se connecter au repo si nécessaire)
``` bash
docker compose pull
docker compose up -d
```
