FROM openjdk:17-alpine
ADD target/blog-0.0.1-SNAPSHOT.jar blog-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","blog-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080