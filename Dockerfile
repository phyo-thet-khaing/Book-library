FROM eclipse-temurin:21-jdk
WORKDIR /app
LABEL maintainer="javaguides-net"

COPY target/Book-library-0.0.1-SNAPSHOT.jar Book-library.jar


ENTRYPOINT ["java", "-jar", "Book-library.jar"]