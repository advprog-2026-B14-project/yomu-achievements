# Gunakan image Java 21 bawaan Linux mini (Alpine)
FROM eclipse-temurin:21-jdk-alpine@sha256:bcc7ec7e8fef937ba9f01ee5f810361d722c6b5dbe19ac188ab7b25c1a4dd2c9

# Bikin folder kerja di dalam container
WORKDIR /app

# Copy file .jar hasil build Spring Boot ke dalam container
COPY build/libs/*SNAPSHOT.jar app.jar

# Buka port 8083 sesuai application.properties kamu
EXPOSE 8083

# Perintah untuk menjalankan aplikasinya
ENTRYPOINT ["java", "-jar", "app.jar"]