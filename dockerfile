# Menggunakan image Java 11 dari OpenJDK
FROM openjdk:11-jdk-slim

# Menentukan direktori kerja di dalam container
WORKDIR /app

# Menyalin file JAR hasil build ke dalam container
COPY target/*.jar app.jar

# Menjalankan aplikasi Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
