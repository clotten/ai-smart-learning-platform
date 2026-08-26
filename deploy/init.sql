/*
 Navicat Premium Dump SQL

 Source Server         : dsharness
 Source Server Type    : MySQL
 Source Server Version : 80200 (8.2.0)
 Source Host           : localhost:3306
 Source Schema         : ai_learning_platform

 Target Server Type    : MySQL
 Target Server Version : 80200 (8.2.0)
 File Encoding         : 65001

 Date: 27/08/2026 02:57:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for answer_record
-- ----------------------------
DROP TABLE IF EXISTS `answer_record`;
CREATE TABLE `answer_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '答题人ID(关联sys_user.id)',
  `question_id` bigint NOT NULL COMMENT '题目ID(关联question.id)',
  `user_answer` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户提交的答案',
  `is_correct` tinyint NOT NULL DEFAULT 0 COMMENT '是否正确: 0错误 1正确',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_question`(`question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '答题记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of answer_record
-- ----------------------------
INSERT INTO `answer_record` VALUES (1, 1, 1, 'B', 1, '2026-08-22 20:03:31', 0);
INSERT INTO `answer_record` VALUES (2, 1, 1, 'A', 0, '2026-08-22 20:03:47', 0);
INSERT INTO `answer_record` VALUES (3, 1, 2, 'CBA', 1, '2026-08-22 20:03:59', 0);
INSERT INTO `answer_record` VALUES (4, 1, 1, 'B', 1, '2026-08-22 20:04:37', 0);
INSERT INTO `answer_record` VALUES (5, 1, 53, 'B', 1, '2026-08-22 23:41:53', 0);
INSERT INTO `answer_record` VALUES (6, 1, 2, 'BAC', 1, '2026-08-22 23:42:19', 0);
INSERT INTO `answer_record` VALUES (7, 2, 2, 'BAC', 1, '2026-08-22 23:42:36', 0);
INSERT INTO `answer_record` VALUES (8, 2, 2, 'BAC', 1, '2026-08-23 20:11:34', 0);
INSERT INTO `answer_record` VALUES (9, 2, 53, 'C', 0, '2026-08-23 23:33:13', 0);
INSERT INTO `answer_record` VALUES (10, 2, 54, 'C', 0, '2026-08-23 23:34:18', 0);
INSERT INTO `answer_record` VALUES (11, 2, 54, 'A', 0, '2026-08-23 23:35:59', 0);
INSERT INTO `answer_record` VALUES (12, 2, 54, 'B', 1, '2026-08-23 23:36:24', 0);
INSERT INTO `answer_record` VALUES (13, 2, 54, 'B', 1, '2026-08-23 23:36:57', 0);
INSERT INTO `answer_record` VALUES (14, 2, 53, 'A', 0, '2026-08-23 23:37:03', 0);
INSERT INTO `answer_record` VALUES (15, 2, 51, 'A', 0, '2026-08-23 23:37:07', 0);
INSERT INTO `answer_record` VALUES (16, 2, 50, 'C', 1, '2026-08-24 00:11:59', 0);
INSERT INTO `answer_record` VALUES (17, 1, 54, 'A', 0, '2026-08-24 14:05:29', 0);

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '题型: 1单选 2多选 3判断',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '未分类' COMMENT '知识点分类(如: Java基础/MySQL/Spring)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题干',
  `options` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选项(JSON格式, 如 {\"A\":\"...\",\"B\":\"...\"}，判断题为空)',
  `answer` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '正确答案(单选\"B\" 多选\"AB\" 判断\"对/错\")',
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '答案解析',
  `difficulty` tinyint NOT NULL DEFAULT 3 COMMENT '难度: 1简单-5困难',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人ID(关联sys_user.id)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 1, 'Java基础', 'Java中基本数据类型共有几种？', '{\"A\":\"7种\",\"B\":\"8种\",\"C\":\"9种\",\"D\":\"10种\"}', 'B', 'Java有8种基本数据类型：byte/short/int/long/float/double/char/boolean', 1, NULL, '2026-08-22 02:27:03', '2026-08-22 02:27:03', 0);
INSERT INTO `question` VALUES (2, 2, 'MySQL', '以下哪些是MySQL的存储引擎？', '{\"A\":\"InnoDB\",\"B\":\"MyISAM\",\"C\":\"Memory\",\"D\":\"Redis\"}', 'ABC', 'InnoDB支持事务，MyISAM不支持，Memory是内存引擎；Redis是独立数据库', 2, NULL, '2026-08-22 02:27:03', '2026-08-22 02:27:03', 0);
INSERT INTO `question` VALUES (3, 3, 'Spring', 'SpringBoot的启动类使用@SpringBootApplication注解标注。', NULL, '对', '@SpringBootApplication是组合注解，包含@SpringBootConfiguration/@EnableAutoConfiguration/@ComponentScan', 1, NULL, '2026-08-22 02:27:03', '2026-08-22 02:27:03', 0);
INSERT INTO `question` VALUES (4, 1, 'Java基础', '以下哪个不属于Java的访问修饰符？', '{\"A\":\"public\",\"B\":\"protected\",\"C\":\"private\",\"D\":\"static\"}', 'D', 'Java访问修饰符包含 public、protected、private，还有默认（不写修饰符）。static属于静态修饰符，不是访问权限修饰符啦。', 1, NULL, '2026-08-22 13:45:16', '2026-08-22 13:49:10', 1);
INSERT INTO `question` VALUES (50, 1, 'Java基础', '下面哪一个不是基本数据类型的包装类？', '{\"A\":\"Integer\",\"B\":\"Float\",\"C\":\"String\",\"D\":\"Boolean\"}', 'C', 'String是字符串类，不属于八大基本类型包装类；Integer(int)、Float(float)、Boolean(boolean)都是包装类。', 2, 2, '2026-08-22 14:02:42', '2026-08-22 14:02:42', 0);
INSERT INTO `question` VALUES (51, 1, 'SpringBoot', 'SpringBoot项目@RestController注解等价于哪两个注解组合？', '{\"A\":\"@Controller + @Service\",\"B\":\"@Controller + @ResponseBody\",\"C\":\"@Component + @ResponseBody\",\"D\":\"@Repository + @Controller\"}', 'B', '@RestController = @Controller + @ResponseBody；直接返回JSON数据，不会走视图解析。', 1, 2, '2026-08-22 14:02:42', '2026-08-22 12:02:42', 0);
INSERT INTO `question` VALUES (53, 1, 'MySQL数据库', 'MySQL中，下面哪种索引查询速度最快？', '{\"A\":\"普通索引\",\"B\":\"主键索引\",\"C\":\"唯一索引\",\"D\":\"全文索引\"}', 'B', '主键索引是聚簇索引，数据和索引在一起，查询效率高于普通索引、唯一索引；全文索引专门用于文本检索。', 2, 2, '2026-08-22 14:22:01', '2026-08-22 14:40:19', 0);
INSERT INTO `question` VALUES (54, 1, 'MySQL', '在MySQL中，关于事务的隔离级别，下列说法正确的是？', '{\"A\":\"READ UNCOMMITTED 允许脏读，但避免了不可重复读\",\"B\":\"READ COMMITTED 避免了脏读，但可能出现不可重复读\",\"C\":\"REPEATABLE READ 避免了不可重复读，但可能出现幻读\",\"D\":\"SERIALIZABLE 避免了幻读，但性能开销最大\"}', 'B', 'MySQL的默认隔离级别是REPEATABLE READ。选项A错误：READ UNCOMMITTED允许脏读，也会出现不可重复读和幻读。选项B正确：READ COMMITTED避免了脏读，但可能出现不可重复读和幻读。选项C错误：REPEATABLE READ在InnoDB中通过MVCC避免了不可重复读，并且通过间隙锁避免了幻读（在MySQL中）。选项D错误：SERIALIZABLE确实避免了幻读且性能开销最大，但题目要求选择\'正确\'的说法，而B的描述完全符合READ COMMITTED的特性，是最准确的。', 2, 2, '2026-08-23 03:03:51', '2026-08-23 03:03:51', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `github_id` bigint NULL DEFAULT NULL COMMENT 'GitHub用户ID',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `role` tinyint NOT NULL DEFAULT 2 COMMENT '角色: 1管理员 2学生',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_github_id`(`github_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'zhangsan', NULL, NULL, '$2a$10$KCGnnBfrHMA1rMnVAUh8l.XE79IPnTMhn2OY9.V2QsDdj4/KPSayC', NULL, 2, NULL, '2026-08-21 23:48:09', '2026-08-21 23:48:09', 0);
INSERT INTO `sys_user` VALUES (2, 'lisi', NULL, NULL, '$2a$10$j0if1oyp5s8GlXVLOgvYfei3ZJR22V9it5E6Ku/KN/iLkP3jTSkSa', NULL, 2, NULL, '2026-08-22 02:56:38', '2026-08-22 02:56:38', 0);
INSERT INTO `sys_user` VALUES (3, 'clotten', 'clotten@qq.com', NULL, NULL, NULL, 2, NULL, '2026-08-26 22:11:17', '2026-08-26 22:11:17', 0);
INSERT INTO `sys_user` VALUES (4, '331308387', '331308387@qq.com', NULL, '$2a$10$1BmkFBp92j6flVbo1iLBkeqpqQgRb3338S3zJmlWjGJLcYLWGZSHK', NULL, 2, NULL, '2026-08-26 22:12:20', '2026-08-26 22:12:20', 0);
INSERT INTO `sys_user` VALUES (6, 'clotten852', NULL, 140622753, NULL, NULL, 2, NULL, '2026-08-27 01:40:08', '2026-08-27 01:40:08', 0);
INSERT INTO `sys_user` VALUES (7, '3363009478', '3363009478@qq.com', NULL, NULL, NULL, 2, NULL, '2026-08-27 01:47:14', '2026-08-27 01:47:14', 0);

SET FOREIGN_KEY_CHECKS = 1;
