package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("C端-查询菜品列表, categoryId={}", categoryId);
        String key = "dish_" + categoryId;
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof List<?>) {
            List<?> cachedList = (List<?>) cached;
            boolean validDishList = cachedList.stream().allMatch(DishVO.class::isInstance);
            if (validDishList && !cachedList.isEmpty()) {
                log.info("C端-从Redis缓存命中菜品数据, categoryId={}, 数量={}", categoryId, cachedList.size());
                @SuppressWarnings("unchecked")
                List<DishVO> cachedDishes = (List<DishVO>) cachedList;
                return Result.success(cachedDishes);
            }
            if (!validDishList) {
                log.warn("C端-菜品缓存格式异常，清理缓存, categoryId={}", categoryId);
            }
            redisTemplate.delete(key);
        } else if (cached != null) {
            log.warn("C端-菜品缓存格式异常，清理缓存, categoryId={}", categoryId);
            redisTemplate.delete(key);
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<DishVO> list = dishService.listWithFlavor(dish);
        if (list != null && !list.isEmpty()) {
            redisTemplate.opsForValue().set(key, list);
        }
        log.info("C端-从数据库查询菜品, categoryId={}, 数量={}", categoryId, (list != null ? list.size() : 0));
        return Result.success(list);
    }

}
