#!/bin/sh
set -e

CA_CERT=/run/secrets/mqtt_ca_cert

if [ -f "$CA_CERT" ]; then
  echo "Import du certificat CA dans le truststore JVM..."

  # Supprime l'alias s'il existe déjà (cas d'un redémarrage du container)
  keytool -delete \
    -alias mosquitto-ca \
    -keystore "$JAVA_HOME/lib/security/cacerts" \
    -storepass changeit \
    2>/dev/null || true

  keytool -importcert \
    -trustcacerts \
    -noprompt \
    -alias mosquitto-ca \
    -file "$CA_CERT" \
    -keystore "$JAVA_HOME/lib/security/cacerts" \
    -storepass changeit
fi

# Lancement de l'application avec su-exec pour exécuter en tant qu'utilisateur non root
exec su-exec appuser java -jar /app/app.jar