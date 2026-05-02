# ==========================================
# TAHAP 1: BUILD (Membuat file .jar)
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy seluruh source code dari GitHub ke dalam container
COPY . .

# Beri hak akses agar script gradlew bisa dieksekusi
RUN chmod +x ./gradlew

# Jalankan perintah build untuk menghasilkan file .jar (tanpa menjalankan test)
RUN ./gradlew clean build -x test

# ==========================================
# TAHAP 2: RUN (Menjalankan aplikasi)
# ==========================================
# Kita pakai JRE (Java Runtime Environment) agar ukuran server lebih ringan
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ambil HANYA file .jar yang sudah jadi dari Tahap 1
COPY --from=build /app/build/libs/*SNAPSHOT.jar app.jar

# Buka port 8083 sesuai application.properties
EXPOSE 8083

# Perintah untuk menjalankan aplikasinya
ENTRYPOINT ["java", "-jar", "app.jar"]