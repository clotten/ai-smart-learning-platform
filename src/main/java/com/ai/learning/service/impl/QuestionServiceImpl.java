package com.ai.learning.service.impl;


import com.ai.learning.common.BusinessException;
import com.ai.learning.entity.Question;
import com.ai.learning.mapper.QuestionMapper;
import com.ai.learning.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

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
        return questionMapper.selectById(id);
    }
}
