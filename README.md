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
``` bash
openssl genrsa -out ca.key 2048
```
``` bash
openssl req -new -x509 -days 365 -key ca.key -out ca.crt -subj "/C=FR/ST=Rhone/L=Lyon/O=MyOrg/CN=MyCA"
```
``` bash
openssl genrsa -out server.key 2048
```
Dans subjectAltName il faut mettre l'IP, DNS du serveur mosquitto.
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
listener 8883 localhost
cafile C:\path\to\mosquitto\certs\ca.crt
certfile C:\path\to\mosquitto\certs\server.crt
keyfile C:\path\to\mosquitto\certs\server.key
require_certificate false
allow_anonymous false
password_file C:\path\to\mosquitto\passwd
```

### Lancer le docker
``` bash
docker run -it -p 8883:8883 -v \chemin\vers\mosquitto\config:/mosquitto/config eclipse-mosquitto
```

### Ajouter un utilisateur
``` bash
docker exec -it <container_id> mosquitto_passwd -b -c passwordfile <user> <password>
```
