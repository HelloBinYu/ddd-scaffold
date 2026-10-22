# ddd-scaffold

[![CI](https://github.com/HelloBinYu/ddd-scaffold/actions/workflows/ci.yml/badge.svg)](https://github.com/HelloBinYu/ddd-scaffold/actions/workflows/ci.yml)

一个开箱即用的 **DDD（领域驱动设计）多模块工程脚手架**，基于 Spring Boot 3 + MyBatis + MySQL + Redis（Redisson）。用它可以快速创建分层清晰、依赖方向正确的新项目。

> 工程结构参考了 [小傅哥 xfg-frame-archetype](https://bugstack.cn/md/road-map/ddd-guide-03.html)，在此基础上升级了依赖、修复了配置问题，并补充了一键初始化、全局异常处理和 CI。

## 技术栈

| 组件 | 版本 |
| --- | --- |
| JDK | 17+ |
| Spring Boot | 3.5.16 |
| MyBatis Spring Boot | 3.0.5 |
| Redisson | 4.8.0 |
| MySQL Connector/J | 由 Spring Boot 管理 |
| Lombok / Guava / Commons Lang3 | 由 Spring Boot 管理 / 33.7.2 / 3.21.0 |
| JUnit | 5（spring-boot-starter-test） |

## 快速开始

### 1. 创建项目

**方式一：GitHub 模板（推荐）**：在仓库页面点击 **Use this template → Create a new repository**，然后克隆你的新仓库。

**方式二：直接克隆**

```bash
git clone https://github.com/HelloBinYu/ddd-scaffold.git order-center
cd order-center
rm -rf .git && git init
```

### 2. 初始化项目名与包名

```bash
# ./init.sh <groupId> <artifactId>
./init.sh cn.bugstack order-center
```

脚本会：

- 把 `com.example` 替换为你的 groupId，并同时作为根包名（`cn.bugstack.domain`、`cn.bugstack.trigger` ...）
- 把 `ddd-scaffold` 替换为你的 artifactId，重命名所有模块目录（`order-center-app`、`order-center-domain` ...）
- 把数据库名 `ddd_scaffold` 替换为 `order_center`
- 生成新项目的 README，并删除 `init.sh` 自身

> Windows 下请在 **Git Bash** 中执行。

### 3. 启动依赖环境并运行

```bash
# 启动 MySQL(13306)、phpMyAdmin(8899)、Redis(16379)、Redis Commander(8081)
docker compose -f docs/dev-ops/docker-compose-environment.yml up -d
# 国内网络可用阿里云镜像版本
# docker compose -f docs/dev-ops/docker-compose-environment-aliyun.yml up -d

./mvnw clean package
java -jar order-center-app/target/order-center-app.jar
```

服务默认监听 `8091` 端口，使用 `dev` 环境配置。

## 工程结构

```
ddd-scaffold
├── ddd-scaffold-app              启动层：Application、配置类、application-*.yml、MyBatis Mapper XML、Dockerfile
├── ddd-scaffold-trigger          触发层：http（Controller、全局异常处理）、job（定时任务）、listener（消息监听）
├── ddd-scaffold-api              接口层：对外接口定义 IXxxService、dto、统一响应 Response
├── ddd-scaffold-domain           领域层：按领域拆分子包（xxx 为示例领域）
│   └── domain/xxx
│       ├── adapter/port          外部服务端口接口 IXxxPort
│       ├── adapter/repository    仓储接口 IXxxRepository
│       ├── model/aggregate       聚合 XxxAggregate
│       ├── model/entity          实体 XxxEntity
│       ├── model/valobj          值对象 XxxVO
│       └── service               领域服务 IXxxService / XxxService
├── ddd-scaffold-infrastructure   基础设施层
│   ├── adapter/port              端口实现 XxxPort
│   ├── adapter/repository        仓储实现 XxxRepository
│   ├── dao / dao/po              MyBatis DAO 接口 IXxxDao、持久化对象 XxxPO
│   ├── gateway / gateway/dto     外部 HTTP/RPC 调用
│   └── redis                     IRedisService / RedisService（基于 Redisson）
├── ddd-scaffold-types            通用类型：常量、ResponseCode、AppException
└── docs/dev-ops                  docker compose 环境、MySQL 初始化脚本、Redis 配置
```

### 模块依赖关系

```
app ──> trigger ──> api ──> types
 │         └──────> domain ──> types
 └──> infrastructure ──> domain
```

- **domain 不依赖 infrastructure**：领域层只定义仓储（repository）与端口（port）接口，由基础设施层实现，实现依赖倒置。
- **trigger 只依赖 domain 与 api**：接口层调用领域服务，不直接访问 DAO。
- **app 负责组装**：引入 trigger 与 infrastructure，Spring 扫描根包完成装配。

### 一次请求的调用链

```
XxxController (trigger) → IXxxService (domain) → IXxxRepository (domain 接口)
                                              ↳ XxxRepository (infrastructure 实现) → IXxxDao → MySQL
```

## 内置能力

- **统一响应**：`Response.success(data)` / `Response.failure(ResponseCode.ILLEGAL_PARAMETER)`
- **业务异常**：在任意层抛出 `new AppException(ResponseCode.UN_ERROR)`，由 `GlobalExceptionHandler` 转为统一响应
- **参数校验**：已引入 `spring-boot-starter-validation`，`@Valid` / `@NotBlank` 校验失败返回 `0002 非法参数` 及具体字段信息
- **线程池**：`ThreadPoolExecutor` Bean，参数见 `thread.pool.executor.config`
- **Redis（Redisson）**：`RedissonClient`（JSON 序列化，配置项 `redis.sdk.config.*`）及 `IRedisService` 封装：读写与过期、SETNX、计数器、Map/List/Set/ZSet/阻塞队列/发布订阅，以及分布式锁、公平锁、读写锁、信号量、闭锁、布隆过滤器。dev 环境默认延迟连接，Redis 未启动也能运行；test/prod 启动即连接，配置错误时启动失败
- **测试**：`ApiTest` 校验 Spring 容器可正常启动，不依赖 MySQL、Redis；`RedisServiceTest` 需要可用的 Redis，执行 `REDIS_TEST=true ./mvnw test`（CI 中已自动启动 Redis 运行）
- **本地缓存**：Guava `Cache<String, String>` Bean
- **日志**：logback 控制台 + 按天/大小滚动文件，INFO、WARN 分文件异步输出到 `logging.file.path`（默认 `./data/log`）

## 多环境配置

| 文件 | 说明 |
| --- | --- |
| `application.yml` | 公共配置：端口、连接池、Redisson 连接池与超时、MyBatis、线程池、日志 |
| `application-dev.yml` | 开发环境（默认）；MySQL、Redis 默认指向本地 docker 的 `127.0.0.1:13306`、`127.0.0.1:16379` |
| `application-test.yml` / `application-prod.yml` | MySQL 通过 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD` 注入，Redis 通过 `REDIS_HOST`、`REDIS_PORT`、`REDIS_PASSWORD` 注入 |

切换环境：`java -jar xxx-app.jar --spring.profiles.active=prod` 或设置环境变量 `SPRING_PROFILES_ACTIVE=prod`。

## 新增一张表的步骤

1. `docs/dev-ops/mysql/sql/*.sql` 中添加建表语句
2. infrastructure：`dao/po/XxxPO` + `dao/IXxxDao`（`@Mapper`）
3. app：`resources/mybatis/mapper/xxx_mapper.xml`

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "https://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.example.infrastructure.dao.IXxxDao">
       <select id="queryById" parameterType="java.lang.Long" resultType="com.example.infrastructure.dao.po.XxxPO">
           SELECT id, xxx_id, create_time, update_time FROM xxx WHERE id = #{id}
       </select>
   </mapper>
   ```

4. domain：`adapter/repository/IXxxRepository` + `model/entity/XxxEntity`
5. infrastructure：`adapter/repository/XxxRepository` 实现接口，完成 PO → Entity 转换

## Docker 部署

```bash
./mvnw clean package
sh ddd-scaffold-app/build.sh                   # 构建镜像 ddd-scaffold-app:1.0.0-SNAPSHOT
docker compose -f docs/dev-ops/docker-compose-environment.yml up -d
docker compose -f docs/dev-ops/docker-compose-app.yml up -d   # prod 环境，连接 compose 网络中的 mysql 与 redis
```

推送到阿里云镜像仓库：在 `ddd-scaffold-app` 下创建 `.local-config`（已被 git 忽略）填写 `ALIYUN_USERNAME`、`ALIYUN_PASSWORD`、`NAMESPACE`，然后执行 `sh push.sh`。

## DDD 参考资料

- [DDD 概念理论](https://bugstack.cn/md/road-map/ddd-guide-01.html)
- [DDD 建模方法](https://bugstack.cn/md/road-map/ddd-guide-02.html)
- [DDD 工程模型](https://bugstack.cn/md/road-map/ddd-guide-03.html)
- [DDD 架构设计](https://bugstack.cn/md/road-map/ddd.html)

## License

[Apache License 2.0](LICENSE)
