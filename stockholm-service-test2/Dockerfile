FROM amazoncorretto:8
EXPOSE 5555
ARG JAR_FILE=./build/libs/stockholm-service-test2.jar
ADD ${JAR_FILE} stockholm-service-test2.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=test","-jar","stockholm-service-test2.jar"]
