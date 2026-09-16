package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

        List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

        /**
         * 批量保存套餐菜品关联数据
         * @param setmealDishes
         */
        void saveBatch(List<SetmealDish> setmealDishes);

        /**
         * 根据套餐id删除套餐菜品关联数据
         * @param setmealId
         */
        void deleteBySetmealId(Long setmealId);

        /**
         * 根据套餐id查询套餐菜品关联数据
         * @param setmealId
         * @return
         */
        @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
        List<SetmealDish> getBySetmealId(Long setmealId);
}
