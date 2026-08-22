package com.ai.learning.controller;


import com.ai.learning.common.Result;
import com.ai.learning.dto.QuestionCreateDTO;
import com.ai.learning.dto.QuestionUpdateDTO;
import com.ai.learning.entity.Question;
import com.ai.learning.service.QuestionService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private  final QuestionService questionService;

    @Operation(summary =  "分页查询题目（支持按题型/分类/关键词筛选）")
    @GetMapping("/page")
    public Result<IPage<Question>> page(
            @Parameter(description = "页码，从1开始")
            @RequestParam(defaultValue = "1")
            int pageNum,
            @Parameter(description = "每页条数")
            @RequestParam(defaultValue = "10")
            int pageSize,
            @Parameter(description = "题型：1单选 2多选 3判断")
            @RequestParam(required = false)
            Integer type,
            @Parameter(description = "分类")
            @RequestParam(required = false)
            String category,
            @Parameter(description = "题干关键词")
            @RequestParam(required = false)
            String keyword){
        return Result.success(questionService.page(pageNum,pageSize,type,category,keyword));
    }

    @Operation(summary = "新增题目")
    @PostMapping
    public Result<Void> add(@RequestBody @Valid QuestionCreateDTO dto, HttpServletRequest request){
        //1.DTO->实体 转换（字段名相同的自动拷贝，id自然保持null）
        Question question = new Question();
        BeanUtils.copyProperties(dto,question);

        //2.创建人从登录token取（安全：不让前端传）
        Object userId = request.getAttribute("userId");
        question.setCreatedBy(userId == null ? null : Long.valueOf(userId.toString()));

        //3.入库
        questionService.add(question);
        return Result.success();
    }

    @Operation(summary = "修改题目")
    @PutMapping
    public Result<Void> update(@RequestBody @Valid QuestionUpdateDTO dto){
        //DTO -> 实体 （id和业务字段一起拷过去）
        Question question = new Question();
        BeanUtils.copyProperties(dto,question);
        questionService.update(question);   //存在性校验在 Service 里
        return Result.success();
    }

    @Operation(summary = "删除题目（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id){
        questionService.delete(id);
        return Result.success();
    }

    @Operation(summary = "题目详细")
    @GetMapping("/{id}")
    public Result<Question> detail(@PathVariable Long id){
        return Result.success(questionService.getById(id));
    }

}
