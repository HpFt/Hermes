FROM openjdk:8-alpine

ADD ../build/libs/Hermes.jar /opt/hermes/app.jar

WORKDIR /app/hermes
CMD ["java", "-jar", "/opt/hermes/app.jar"]