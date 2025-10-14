FROM maven:3.8.6-openjdk-11 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean install -DskipTests

FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=build /app/target/banking-system-1.0-SNAPSHOT.jar banking-system.jar

EXPOSE 8080

CMD ["java", "-jar", "banking-system.jar"]

