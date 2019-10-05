# copy java jdk
FROM openjdk:oracle

# copy JAR file
COPY target/api-4155-0.0.1-SNAPSHOT.jar app/api-4155-0.0.1-SNAPSHOT.jar

# expose entry port
EXPOSE 8080

# start
CMD ["java", "-jar", "app/api-4155-0.0.1-SNAPSHOT.jar"]