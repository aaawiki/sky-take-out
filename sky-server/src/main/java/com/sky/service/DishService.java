package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    public void saveWithFlavor(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO getById(Long id);

    /**
     * 修改菜品及其口味
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类id查询菜品列表
     */
    List<Dish> listByCategoryId(Long categoryId);

    /**
     * 菜品起售/停售
     */
    void startOrStop(Integer status, Long id);
}
