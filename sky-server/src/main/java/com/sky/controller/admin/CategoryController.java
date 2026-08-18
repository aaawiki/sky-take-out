package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.impl.EmployeeServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    private EmployeeServiceImpl employeeServiceImpl;

    @GetMapping("/page")
    @ApiOperation("分页查询分类")
    public Result page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类");
        return Result.success(categoryService.page(categoryPageQueryDTO));
    }
    @PutMapping
    @ApiOperation("修改分类")
    public Result change(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类");
        categoryService.change(categoryDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("启用或禁用分类")
    public Result startOrStop(@PathVariable Integer status, long id){
        log.info("启用或禁用分类status:{},id:{}",status,id);
        categoryService.startOrStop(status,id);
        return Result.success();
    }
    @PostMapping()
    @ApiOperation("新增分类")
    public Result add(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类");
        categoryService.add(categoryDTO);
        return Result.success();
    }
    @DeleteMapping()
    @ApiOperation("删除分类")
    public Result delete(Integer id) {
        log.info("删除分类：{}", id);
        categoryService.delete(id);
        return Result.success();
    }
    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
