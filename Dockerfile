###
# Image pour la compilation
FROM maven:3.9.11-eclipse-temurin-21 AS builder
WORKDIR /build/

# On lance la compilation Java
# On débute par une mise en cache docker des dépendances Java
# cf https://www.baeldung.com/ops/docker-cache-maven-dependencies
# et la compilation du code Java
COPY ./   /build/

RUN mvn --batch-mode \
        -Dmaven.test.skip=true \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package


###
# Image pour le module API
FROM ossyupiik/java:21.0.8 AS cerclebacon-api
WORKDIR /

COPY --from=builder /build/target/*.jar /cerclebacon-api.jar

ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080
CMD ["java", "-jar", "cerclebacon-api.jar"]
