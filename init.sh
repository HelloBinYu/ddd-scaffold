#!/usr/bin/env bash
# 初始化脚手架：将 groupId/包名 com.example 与项目名 ddd-scaffold 替换为你的项目信息
# 用法：./init.sh <groupId> <artifactId>
# 示例：./init.sh cn.bugstack order-center
#   - groupId 同时作为根包名，模块包为 <groupId>.api / .domain / .infrastructure ...
#   - artifactId 作为项目名与模块前缀，模块为 <artifactId>-app / -domain ...
# Windows 下请在 Git Bash 中执行
set -euo pipefail

OLD_GROUP="com.example"
OLD_ARTIFACT="ddd-scaffold"
OLD_ARTIFACT_US="ddd_scaffold"

if [ $# -ne 2 ]; then
  echo "用法：./init.sh <groupId> <artifactId>"
  echo "示例：./init.sh cn.bugstack order-center"
  exit 1
fi

GROUP="$1"
ARTIFACT="$2"
ARTIFACT_US="${ARTIFACT//-/_}"

if ! [[ "$GROUP" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]]; then
  echo "groupId 不合法：$GROUP（需为小写的包名格式，如 cn.bugstack）"
  exit 1
fi
if ! [[ "$ARTIFACT" =~ ^[a-z][a-z0-9]*(-[a-z0-9]+)*$ ]]; then
  echo "artifactId 不合法：$ARTIFACT（需为小写字母、数字和中划线，如 order-center）"
  exit 1
fi

cd "$(dirname "$0")"

if [ ! -d "${OLD_ARTIFACT}-app" ]; then
  echo "未找到 ${OLD_ARTIFACT}-app 目录，项目可能已经初始化过"
  exit 1
fi

echo "==> groupId/包名：${OLD_GROUP} -> ${GROUP}"
echo "==> artifactId：  ${OLD_ARTIFACT} -> ${ARTIFACT}"

# 1. 替换文件内容
export GROUP ARTIFACT ARTIFACT_US
find . -type f \
  -not -path './.git/*' \
  -not -path '*/target/*' \
  -not -path '*/data/log/*' \
  -not -name 'init.sh' \
  -not -name '*.jar' \
  -print0 | xargs -0 perl -pi -e '
    s/com\.example/$ENV{GROUP}/g;
    s/ddd-scaffold/$ENV{ARTIFACT}/g;
    s/ddd_scaffold/$ENV{ARTIFACT_US}/g;
  '

# 2. 移动包目录 com/example -> 新包路径
NEW_PATH="${GROUP//.//}"
for src in */src/main/java */src/test/java; do
  [ -d "$src/com/example" ] || continue
  tmp="$src/.init_pkg_tmp"
  mv "$src/com/example" "$tmp"
  rmdir "$src/com" 2>/dev/null || true
  mkdir -p "$src/$NEW_PATH"
  mv "$tmp"/* "$src/$NEW_PATH"/
  rmdir "$tmp"
done

# 3. 重命名模块目录与文件
for dir in "${OLD_ARTIFACT}"-*/; do
  dir="${dir%/}"
  mv "$dir" "${ARTIFACT}${dir#"$OLD_ARTIFACT"}"
done
mv "docs/dev-ops/mysql/sql/${OLD_ARTIFACT_US}.sql" "docs/dev-ops/mysql/sql/${ARTIFACT_US}.sql"

# 4. 生成项目 README，移除脚手架自身的说明与初始化脚本
cat > README.md <<EOF
# ${ARTIFACT}

基于 [ddd-scaffold](https://github.com/HelloBinYu/ddd-scaffold) 创建的 DDD 分层项目。

## 模块

| 模块 | 说明 |
| --- | --- |
| ${ARTIFACT}-app | 启动层；Application、配置类、配置文件、MyBatis Mapper XML |
| ${ARTIFACT}-trigger | 触发层；HTTP 接口、定时任务、消息监听 |
| ${ARTIFACT}-api | 接口层；对外接口定义与 DTO |
| ${ARTIFACT}-domain | 领域层；按领域划分的实体、聚合、值对象、领域服务、仓储与端口接口 |
| ${ARTIFACT}-infrastructure | 基础设施层；仓储与端口实现、DAO、PO、外部网关、Redis |
| ${ARTIFACT}-types | 通用类型；常量、枚举、异常 |

## 本地运行

\`\`\`bash
# 启动 MySQL、Redis
docker compose -f docs/dev-ops/docker-compose-environment.yml up -d
# 构建并运行
./mvnw clean package
java -jar ${ARTIFACT}-app/target/${ARTIFACT}-app.jar
\`\`\`
EOF
rm -f init.sh .github/workflows/template-check.yml

echo "==> 初始化完成，可执行 ./mvnw clean verify 验证构建"
