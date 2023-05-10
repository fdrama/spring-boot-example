FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/spring-cache-example-0.0.1-SNAPSHOT.jar /data/java/web/app.jar
ENTRYPOINT ["java","-jar","/data/java/web/app.jar"]