FROM maven:3.9.9-amazoncorretto-17-al2023 AS build

WORKDIR /app

COPY . /app

RUN mvn clean package

FROM openjdk:17-ea-17-jdk-slim-buster

WORKDIR /app

COPY --from=build /app/target/*.jar veiculoApp.jar

EXPOSE 8080

#CMD ["java", "-jar", "veiculoApp.jar"]
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "veiculoApp.jar"]
