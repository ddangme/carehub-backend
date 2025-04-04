# 빌드 단계: Gradle을 사용하여 애플리케이션 빌드
FROM gradle:8.8-jdk21 AS builder

# 작업 디렉토리 설정
WORKDIR /home/gradle/project

# Gradle 캐시를 활용하기 위해 필요한 파일만 먼저 복사
COPY build.gradle settings.gradle gradlew gradlew.bat /home/gradle/project/
COPY gradle /home/gradle/project/gradle

# 의존성 다운로드 (캐싱 목적)
RUN gradle build -x test --no-daemon || return 0

# 전체 소스 코드 복사
COPY . /home/gradle/project

# 애플리케이션 빌드
RUN gradle build -x test --no-daemon

# 실행 단계: 경량화된 OpenJDK 이미지를 사용하여 애플리케이션 실행
FROM openjdk:21-jdk-slim

# 타임존 설정 (옵션)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 비루트 사용자 생성 및 변경
RUN useradd -ms /bin/bash appuser
USER appuser

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]