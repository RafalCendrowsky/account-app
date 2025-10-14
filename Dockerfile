FROM gradle:9-jdk25 AS builder
WORKDIR /home/gradle/project
COPY build.gradle settings.gradle gradle/ gradlew ./
RUN gradle --no-daemon dependencies
COPY src ./src
RUN gradle --no-daemon build

FROM amazoncorretto:25 AS runtime
WORKDIR /app
COPY --from=builder /home/gradle/project/build/libs/*.jar ./app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
