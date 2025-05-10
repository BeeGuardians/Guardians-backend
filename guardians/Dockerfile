# 1단계: Build Stage
FROM gradle:8.5-jdk17 AS build

ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.workers.max=2 -Dorg.gradle.configureondemand=true"

WORKDIR /app

# 필요한 파일만 먼저 복사 (Gradle 캐시 최적화)
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle
COPY gradlew gradlew.bat /app/

# 종속성 다운로드 및 캐시 생성
RUN ./gradlew build -x test --no-daemon --parallel --build-cache --configuration-cache --quiet

# 전체 소스 복사
COPY . /app

# 실제 빌드 (테스트 제외)
RUN ./gradlew clean build -x test --no-daemon --parallel --quiet

# 2단계: Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# 앱 JAR 복사
COPY --from=build /app/build/libs/guardians-0.0.1-SNAPSHOT.jar /app/app.jar

# entrypoint.sh 복사
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# 엔트리포인트 변경
ENTRYPOINT ["/app/docker-entrypoint.sh"]
