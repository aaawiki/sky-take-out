package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    DishService dishService;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询:{}",dishPageQueryDTO);
         return Result.success(dishService.page(dishPageQueryDTO));
    }

    @DeleteMapping()
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除:{}",ids);
        dishService.delete(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询菜品")
    public Result getById(@PathVariable Long id){
        log.info("根据ID查询菜品:{}",id);
       return Result.success(dishService.getById(id));
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(@RequestParam Long categoryId){
        log.info("根据分类id查询菜品:{}",categoryId);
        List<Dish> list = dishService.listByCategoryId(categoryId);
        return Result.success(list);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("菜品起售停售：status:{},id:{}",status,id);
        dishService.startOrStop(status, id);
        return Result.success();
    }
}
