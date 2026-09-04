package com.ai.learning.question.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目实体类：对应数据库question表
 */
@Data
@TableName("question")
public class Question {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer type;       //题型：1单选 2多选 3判断

    private String category;    //知识点分类

    private String content;     //题干

    private String options;     //选项（JSON）

    private String answer;      //正确答案

    private String analysis;    //答案解析

    private Integer difficulty; //难度1-5

    private Long createdBy;     //创建人ID

    private LocalDateTime createdAt;    //创建时间

    private LocalDateTime updatedAt;    //更新时间

    @TableLogic
    private Integer deleted;
}
