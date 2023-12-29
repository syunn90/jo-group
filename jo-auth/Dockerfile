FROM openjdk:8

WORKDIR /auth

ARG JAR_FILE=target/auth.jar

COPY ${JAR_FILE} app.jar

EXPOSE 1000

ENV TZ=Asia/Shanghai

ENTRYPOINT ["java","-jar", "app.jar", "-Xms32M -Xmx32M -XX:PermSize=32M -XX:MaxPermSize=64M -Djava.security.egd=file:/dev/./urandom"]