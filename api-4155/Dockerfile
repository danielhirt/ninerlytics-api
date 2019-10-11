# copy java jdk
FROM openjdk:oracle

# copy JAR file
COPY target/api-4155-1.0.2-SPRT-1.jar app/api-4155-1.0.2-SPRT-1.jar

# expose entry port
EXPOSE 8080

# start
CMD ["java", "-jar", "app/api-4155-1.0.2-SPRT-1.jar"]