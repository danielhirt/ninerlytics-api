# copy java jdk
FROM openjdk:oracle

# copy JAR file
COPY target/api-4155-1.0.4-SPRT-2.jar app/api-4155-1.0.4-SPRT-2.jar

# expose entry port
EXPOSE 8080

# start
CMD ["java", "-jar", "app/api-4155-1.0.4-SPRT-2.jar"]