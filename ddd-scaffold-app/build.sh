#!/bin/bash
# 构建镜像；在 ddd-scaffold-app 目录下执行，执行前先 mvn clean package
set -e
cd "$(dirname "$0")"

IMAGE_NAME="ddd-scaffold-app"
IMAGE_TAG="1.0.0-SNAPSHOT"

# 普通镜像构建，随系统架构构建 amd/arm
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f ./Dockerfile .

# 同时构建 amd64、arm64 镜像并推送（需先 docker login）
# docker buildx build --platform linux/amd64,linux/arm64 -t <registry>/<namespace>/${IMAGE_NAME}:${IMAGE_TAG} -f ./Dockerfile . --push
