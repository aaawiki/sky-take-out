package com.sky.controller.user;

import com.sky.constant.RedisKeyConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-店铺相关接口
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "用户端-店铺相关接口")
@Slf4j
public class ShopController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询店铺营业状态
     *
     * @return 1表示营业中，0表示打烊
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus() {
        String status = stringRedisTemplate.opsForValue().get(RedisKeyConstant.SHOP_STATUS);
        log.info("查询店铺营业状态为：{}", "1".equals(status) ? "营业中" : "打烊中");
        // 未设置过状态时默认返回打烊
        return Result.success("1".equals(status) ? 1 : 0);
    }
}
