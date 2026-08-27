package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;
    /**
     * 保存菜品及其口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish=new Dish();
        List<DishFlavor> dishFlavors=dishDTO.getFlavors();
        BeanUtils.copyProperties(dishDTO,dish);
        //1.插入一条菜品数据
        dishMapper.insert(dish);
        //把插入菜品的主键值给获取到

        //2.插入n条口味数据
        if(dishFlavors!=null && dishFlavors.size()>0) {
            dishFlavors.forEach(dishFlavor -> {dishFlavor.setDishId(dish.getId());});
            dishFlavorMapper.insertBatch(dishFlavors);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page=dishMapper.page(dishPageQueryDTO);
        return new PageResult(page.getPages(),page.getResult());
    }

    @Override
    public void delete(List<Long> ids) {
        //遍历ids，根据id查询菜品，若status等于1（起售中）则抛出业务异常，提示“起售中的菜品不能删除”
        for(Long id :ids){
            if(dishMapper.getById(id).getStatus()== StatusConstant.ENABLE)
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        //判断dish与套餐是否关联
        List<Long> list=setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(list!=null && !list.isEmpty())
            throw  new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

            dishMapper.deleteByIds(ids);
            dishFlavorMapper.deleteByDishIds(ids);

    }


}
