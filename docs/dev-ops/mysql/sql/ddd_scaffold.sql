-- 初始化脚本；MySQL 容器首次启动时自动执行（挂载到 /docker-entrypoint-initdb.d）
-- 如需重新执行，删除 mysql 容器及其数据卷后重新启动

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `ddd_scaffold` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `ddd_scaffold`;

-- 示例表；按需修改或删除
-- CREATE TABLE IF NOT EXISTS `xxx` (
--   `id`          bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
--   `xxx_id`      varchar(32)     NOT NULL COMMENT '业务ID',
--   `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--   `update_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--   PRIMARY KEY (`id`),
--   UNIQUE KEY `uq_xxx_id` (`xxx_id`)
-- ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='示例表';
