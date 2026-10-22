#!/bin/bash
# 推送镜像到阿里云容器镜像服务 https://cr.console.aliyun.com/cn-hangzhou/instance/credentials
# 在当前目录创建 .local-config（已加入 .gitignore），内容：
#   ALIYUN_USERNAME=xxx
#   ALIYUN_PASSWORD=xxx
#   NAMESPACE=xxx
set -e
cd "$(dirname "$0")"

ALIYUN_REGISTRY="registry.cn-hangzhou.aliyuncs.com"
IMAGE_NAME="ddd-scaffold-app"
IMAGE_TAG="1.0.0-SNAPSHOT"

if [ -f ".local-config" ]; then
  source .local-config
else
  echo ".local-config 文件不存在，请创建并填写 ALIYUN_USERNAME、ALIYUN_PASSWORD 和 NAMESPACE"
  exit 1
fi

TARGET="${ALIYUN_REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}"

echo "Logging into Aliyun Docker Registry..."
echo "${ALIYUN_PASSWORD}" | docker login --username="${ALIYUN_USERNAME}" --password-stdin ${ALIYUN_REGISTRY}

echo "Tagging and pushing ${TARGET} ..."
docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${TARGET}
docker push ${TARGET}

echo "Docker image pushed successfully!"
echo "拉取镜像：docker pull ${TARGET}"
echo "本地标签：docker tag ${TARGET} ${IMAGE_NAME}:${IMAGE_TAG}"

docker logout ${ALIYUN_REGISTRY}
