package com.ai.learning.answer.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 答题记录实体类：对应 answer_record 表
 */
@Data
@TableName("answer_record")
public class AnswerRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;        //答题人ID

    private Long questionId;    //题目ID

    private String userAnswer;  //用户提交的答案

    private Integer isCorrect;  //是否正确：0错 1对

    private LocalDateTime createdAt;    //答题时间

    @TableLogic
    private Integer deleted;


}
