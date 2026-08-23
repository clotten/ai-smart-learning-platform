package com.ai.learning.service.impl;


import com.ai.learning.VO.AnswerResultVO;
import com.ai.learning.VO.AnswerStatsVO;
import com.ai.learning.VO.WrongQuestionVO;
import com.ai.learning.annotation.RateLimit;
import com.ai.learning.common.BusinessException;
import com.ai.learning.dto.AnswerSubmitDTO;
import com.ai.learning.entity.AnswerRecord;
import com.ai.learning.entity.Question;
import com.ai.learning.mapper.AnswerRecordMapper;
import com.ai.learning.mapper.QuestionMapper;
import com.ai.learning.service.AnswerService;
import com.ai.learning.service.RateLimitService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final QuestionMapper questionMapper;
    private final AnswerRecordMapper answerRecordMapper;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
    private final RateLimitService rateLimitService;

    /**
     * 提交答案：判分 + 存记录
     * Transactional:记录插入（和以后可能的统计更新）要么全成、要么全回滚
     */
    @Override
    @Transactional
    @RateLimit(business = "answer")
    public AnswerResultVO submit(AnswerSubmitDTO dto,Long userId){

        if(!rateLimitService.tryDedup(userId, dto.getQuestionId(), Duration.ofSeconds(2))){
            throw new BusinessException("提交过于频繁，请稍后再试");
        }

        //1.查题目（不存在报错）
        Question question = questionMapper.selectById(dto.getQuestionId());
        if(question == null){
            throw new BusinessException("题目不存在");
        }

        //2.判分：答案归一化后比较（解决多选题“AB”和“BA的顺序问题”）
        boolean correct = normalize(dto.getUserAnswer()).equals(normalize(question.getAnswer()));

        //答对 -> 排行榜刷题数 +1 （ZINCRBY）
        if(correct){
            redisTemplate.opsForZSet().incrementScore("learn:leaderboard",userId.toString(),1);
        }
        //3.存答题记录
        AnswerRecord record = new AnswerRecord();
        record.setUserId(userId);
        record.setQuestionId(question.getId());
        record.setUserAnswer(dto.getUserAnswer());
        record.setIsCorrect(correct ? 1 : 0);
        answerRecordMapper.insert(record);

        //4.组VO返回（题目 + 对错 +解析，答对也返回解析方便学习）
        AnswerResultVO vo =new AnswerResultVO();
        vo.setQuestionId(question.getId());
        vo.setContent(question.getContent());
        vo.setUserAnswer(dto.getUserAnswer());
        vo.setCorrectAnswer(question.getAnswer());
        vo.setCorrect(correct);
        vo.setAnalysis(question.getAnalysis());
        return vo;
    }

    /**
     * 答案归一化：去空格 + 大写 + 字符排序 -> “ba”和“AB”都变成“AB”
     */
    private String normalize(String answer){
        if(answer == null) return "";
        String s = answer.trim().toUpperCase();
        char[] chars = s.toCharArray();
        java.util.Arrays.sort(chars);
        return new String(chars);
    }

    @Override
    public IPage<WrongQuestionVO> wrongList(Long userId, int pageNum, int pageSize){
        //1.取该用户所有答题记录（id 倒叙 = 时间倒序）
        List<AnswerRecord> records = answerRecordMapper.selectList(
                new QueryWrapper<AnswerRecord>()
                        .eq("user_id",userId)
                        .orderByDesc("id"));

        //2.每道题只保留“最新一条”（第一次遇到的即是最新），且最新一条是错的 -> 错题
        List<WrongQuestionVO> wrongList = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        for(AnswerRecord r : records){
            if (seen.contains(r.getQuestionId())) continue;
            seen.add(r.getQuestionId());
            if(r.getIsCorrect() !=0 ) continue; //最新记录答对了 -> 不是错题

            Question q = questionMapper.selectById(r.getQuestionId());
            if(q == null) continue;
            WrongQuestionVO vo =new WrongQuestionVO();
            vo.setQuestionId(q.getId());
            vo.setContent(q.getContent());
            vo.setMyAnswer(r.getUserAnswer());
            vo.setCorrectAnswer(q.getAnswer());
            vo.setAnalysis(q.getAnalysis());
            wrongList.add(vo);
        }

        //3.手动分页（教学班： 正式项目用SQL窗口函数ROW_NUMBER优化功能）
        int total = wrongList.size();
        int from = Math.min((pageNum -1) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        Page<WrongQuestionVO> result = new Page<>(pageNum, pageSize, total);
        result.setRecords(new ArrayList<>(wrongList.subList(from, to)));
        return result;
    }

    @Override
    public AnswerStatsVO stats(Long userId){
        Long total = answerRecordMapper.selectCount(
                new QueryWrapper<AnswerRecord>().eq("user_id",userId));
        Long correctCount = answerRecordMapper.selectCount(
                new QueryWrapper<AnswerRecord>().eq("user_id",userId).eq("is_correct",1));
        AnswerStatsVO vo = new AnswerStatsVO();
        vo.setTotalCount(total);
        vo.setCorrectCount(correctCount);
        //正确率 = 答对/总数，保留1位小数（除以0防护）
        vo.setAccuracy(total == 0 ? 0.0 : Math.round(correctCount * 1000.0 / total) / 10.0);
        return vo;
    }
}
