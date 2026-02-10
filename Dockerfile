FROM maven:3.8.5-openjdk-17 AS build
COPY . .
# This line tells Docker to go into the demo folder
WORKDIR /demo
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
# We updated the path here to match the demo folder
COPY --from=build /demo/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
