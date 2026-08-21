-- ============================================
-- AI 智能学习平台 数据库初始化脚本
-- 用 Navicat 连接 MySQL 后执行本文件
-- ============================================

CREATE DATABASE IF NOT EXISTS ai_learning_platform
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE ai_learning_platform;

-- ---------- 用户表 ----------
-- role: 1=管理员 2=学生
CREATE TABLE IF NOT EXISTS sys_user (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  username   VARCHAR(50)  NOT NULL COMMENT '用户名',
  password   VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密后的密码',
  nickname   VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  role       TINYINT      NOT NULL DEFAULT 2 COMMENT '角色: 1管理员 2学生',
  avatar     VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted    TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 默认管理员账号：admin / admin123 （密码是 BCrypt 加密后的，先插入一个占位，后续用代码注册）
-- 说明：BCrypt 哈希每次生成都不同，这里先不插密码，等启动项目后用注册接口创建
