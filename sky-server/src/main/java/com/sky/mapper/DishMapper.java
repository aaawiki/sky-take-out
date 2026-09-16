package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    @AutoFill(value = OperationType.INSERT)
    public void insert(Dish dish);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    void deleteByIds(List<Long> ids);

    /**
     * 根据分类id查询菜品列表
     */
    List<Dish> listByCategoryId(Long categoryId);

    /**
     * 根据id查询菜品（含分类名称）
     */
    DishVO getByIdWithCategory(Long id);

    /**
     * 动态条件查询菜品
     */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询菜品
     */
    @Select("select a.* from dish a inner join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

    /**
     * 动态条件统计菜品数量
     */
    Integer countByMap(Map map);
}