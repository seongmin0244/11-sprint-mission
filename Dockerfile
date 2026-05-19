FROM amazoncorretto:17

WORKDIR /app

# 소스 코드 전체 복사 (.dockerignore 파일 설정)
COPY . .

# Gradle Wrapper를 사용하여 애플리케이션 빌드 (테스트는 생략하여 빌드 속도 단축)
RUN ./gradlew clean build -x test

EXPOSE 80

ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

ENV JVM_OPTS=""

# 애플리케이션 실행 명령어 설정 (환경 변수를 읽기 위해 sh -c 사용)
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]