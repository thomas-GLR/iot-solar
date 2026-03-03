# Étape 1 : Build
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Copie les fichiers de config Gradle en premier (optimise le cache)
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Télécharge les dépendances en cache séparé
RUN gradle dependencies --no-daemon

COPY src ./src
RUN gradle bootJar --no-daemon -x test

# Étape 2 : Image finale légère
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Installe su-exec pour changer d'utilisateur dans le entrypoint
RUN apk add --no-cache su-exec

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# keytool a besoin d'écrire dans JAVA_HOME, donc on reste root jusqu'au lancement
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]