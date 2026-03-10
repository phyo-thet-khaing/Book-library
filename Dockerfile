FROM eclipse-temurin:21-jdk
WORKDIR /app
LABEL maintainer ="javaguides-net"
ADD   target/Book-library-0.0.1-SNAPSHOT.jar Book-library.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Book-library.jar"] 
#  Dockerfile
<<<<<<< HEAD:DockerFile
# DockerFile
=======
>>>>>>> 0f9e652d59ede3c590732e0fb5e5e7b43382a018:Dockerfile
