package com.ai.learning.service.impl;


import com.ai.learning.common.BusinessException;
import com.ai.learning.entity.Question;
import com.ai.learning.mapper.QuestionMapper;
import com.ai.learning.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;

    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private static final String QUESTION_CACHE_KEY = "learn:questin:";
    @Override
    public void add(Question question){
        questionMapper.insert(question);
        log.info("新增体题目:id={},分类={}",question.getId(),question.getCategory());
    }

    @Override
    public void update(Question question){
        if(question.getId() == null){
            throw new BusinessException("题目不能为空");
        }
        //业务校验：题目不存在就报错（而不是静默改0行）
        if(questionMapper.selectById(question.getId()) == null){
            throw new BusinessException("题目不存在");
        }
        questionMapper.updateById(question);
    }

    @Override
    public void delete(Long id){
        if(questionMapper.selectById(id) == null){
            throw new BusinessException("题目不存在");
        }
        questionMapper.deleteById(id);  //@TableLogic 自动变成 UPDATE deleted=1
    }

    @Override
    public IPage<Question> page(int pageNum, int pageSize, Integer type, String category, String keyword){
        //1.分页参数：第几页、每页几条
        Page<Question> page = new Page<>(pageNum,pageSize);

        //2.条件构造器：有值才拼条件（动态SQL）
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        if(type != null){
            wrapper.eq("type",type);            //按题类型精确查
        }
        if(StringUtils.hasText(category)){
            wrapper.eq("category",category);    //按分类精确查
        }
        if (StringUtils.hasText(keyword)){
            wrapper.like("content",keyword);    //题干模糊搜（自动拼 %keyword%）
        }
        wrapper.orderByDesc("id");              //新题在前

        //3.执行分页查询
        return questionMapper.selectPage(page,wrapper);
    }

    @Override
    public Question getById(Long id){
        //1.先查缓存
        String key = QUESTION_CACHE_KEY + id;
        String json = redisTemplate.opsForValue().get(key);
        if(json !=null) {
            if ("NULL".equals(json)) {
                throw new BusinessException("题目不存在");   //穿透保护：空值缓存命中
            }
            try {
                return objectMapper.readValue(json, Question.class);

            } catch (Exception e) {
                throw new BusinessException("缓存数据解析失败");
            }
        }

        //2.缓存没有->查数据库
        Question question = questionMapper.selectById(id);

        if(question == null){
            //3.防穿透：查不到也缓存空值（短过期2分钟）
            redisTemplate.opsForValue().set(key,"NULL",Duration.ofMinutes(2));
            throw new BusinessException("题目不存在");
        }

        //4.回填缓存：30分钟 + 随机 0-5 分钟（防雪崩）
        long expire = 30 + ThreadLocalRandom.current().nextInt(6);
        try{
            redisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(question),Duration.ofMinutes(expire));
        }catch(Exception e){
            throw new BusinessException("缓存写入失败");
        }
        return question;
    }
}
