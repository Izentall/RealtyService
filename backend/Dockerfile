# Шаг 1: Используем официальный образ Gradle для сборки приложения
FROM gradle:7.4.2-jdk17 as builder

# Устанавливаем рабочую директорию внутри контейнера
WORKDIR /home/gradle/project

# Копируем файлы проекта в контейнер
COPY . .

# Запускаем сборку проекта
RUN gradle build --no-daemon

# Шаг 2: Создаем финальный образ, содержащий только исполняемое приложение
FROM openjdk:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем скомпилированный JAR из предыдущего шага в новый образ
COPY --from=builder /home/gradle/project/build/libs/backend-1.0.0-SNAPSHOT.jar /app/backend-1.0.0-SNAPSHOT.jar

# Определяем команду для запуска приложения
CMD ["java", "-jar", "/app/backend-1.0.0-SNAPSHOT.jar"]