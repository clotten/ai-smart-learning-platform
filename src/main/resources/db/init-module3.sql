-- ============================================
-- 模块三：刷题 + 错题本
-- 数据库脚本（Navicat 执行）
-- ============================================

USE ai_learning_platform;

-- ---------- 答题记录表 ----------
-- 每次答题存一条：谁、答了哪题、答了什么、对不对
CREATE TABLE IF NOT EXISTS answer_record (
  id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  user_id     BIGINT       NOT NULL COMMENT '答题人ID(关联sys_user.id)',
  question_id BIGINT       NOT NULL COMMENT '题目ID(关联question.id)',
  user_answer VARCHAR(10)  NOT NULL COMMENT '用户提交的答案',
  is_correct  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否正确: 0错误 1正确',
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
  deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
  PRIMARY KEY (id),
  KEY idx_user (user_id),          -- 按人查：统计、错题本
  KEY idx_question (question_id)   -- 按题查
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '答题记录表';
