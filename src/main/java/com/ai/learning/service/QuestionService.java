package com.ai.learning.service;

import com.ai.learning.entity.Question;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface QuestionService {

    /**
     * 新增题目
     */
    void add(Question question);

    /**
     * 修改题目
     */
    void update(Question question);

    /**
     * 删除题目（逻辑删除）
     */
    void delete(Long id);

    /**
     * 分页 + 条件查询
     */
    IPage<Question> page(int pageNum, int pageSize, Integer type, String category, String keyword);

    /**
     * 根据id查询题目
     */
    Question getById(Long id);
}
