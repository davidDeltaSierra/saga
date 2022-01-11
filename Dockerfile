FROM openjdk:17
COPY /target/saga.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]