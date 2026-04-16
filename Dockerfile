# Gunakan image Java 21 bawaan Linux mini (Alpine)
FROM eclipse-temurin:21-jdk-alpine

# Bikin folder kerja di dalam container
WORKDIR /app

# Copy file .jar hasil build Spring Boot ke dalam container
COPY build/libs/*SNAPSHOT.jar app.jar

# Buka port 8083 sesuai application.properties kamu
EXPOSE 8083

# Perintah untuk menjalankan aplikasinya
ENTRYPOINT ["java", "-jar", "app.jar"]