# 基础镜像；构建前先执行 mvn clean package 生成 target/ddd-scaffold-app.jar
FROM eclipse-temurin:17-jre

# 时区
ENV TZ=Asia/Shanghai
# JVM 参数，如 -Xms512m -Xmx512m
ENV JAVA_OPTS=""
# 应用参数，如 --spring.profiles.active=prod
ENV PARAMS=""
# 日志目录，对应 docker-compose 中挂载的 /data/log
ENV LOGGING_FILE_PATH=/data/log

COPY target/ddd-scaffold-app.jar /ddd-scaffold-app.jar

EXPOSE 8091

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /ddd-scaffold-app.jar $PARAMS"]
