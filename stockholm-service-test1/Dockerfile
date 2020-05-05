FROM amazoncorretto:8
EXPOSE 5555
ARG JAR_FILE=./build/libs/stockholm-service-test1.jar
ADD ${JAR_FILE} stockholm-service-test1.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=test1","-jar","stockholm-service-test1.jar"]
