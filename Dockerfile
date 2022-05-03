FROM openjdk:11
EXPOSE 8092
ADD target/microservicio-user.jar microservicio-user.jar
ENTRYPOINT ["java","-jar","/microservicio-user.jar"]