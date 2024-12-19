# Step 1: Java 21 기반 이미지를 사용
FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

# Step 2: 애플리케이션 JAR 파일 복사
COPY build/libs/concert-0.0.1-SNAPSHOT.jar ./concert-api.jar

# Step 3: 포트 노출
EXPOSE 8080

# Step 4: JAR 파일 실행
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
CMD ["java","-jar","concert-api.jar"]