FROM amazoncorretto:8
EXPOSE 8888
ARG JAR_FILE=./build/libs/stockholm-service-dev.jar
ADD ${JAR_FILE} stockholm-service-dev.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=dev","-jar","stockholm-service-dev.jar"]
