package com.sky.controller.admin;

import com.sky.constant.RedisKeyConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-店铺相关接口
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "管理端-店铺相关接口")
@Slf4j
public class ShopController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置店铺营业状态
     *
     * @param status 1表示营业中，0表示打烊
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态为：{}", status == 1 ? "营业中" : "打烊中");
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.SHOP_STATUS, String.valueOf(status));
        return Result.success();
    }

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

    /**
     * 设置店铺联系电话
     *
     * @param phone 店铺联系电话
     * @return
     */
    @PutMapping("/phone")
    @ApiOperation("设置店铺联系电话")
    public Result setPhone(@RequestParam String phone) {
        log.info("设置店铺联系电话为：{}", phone);
        stringRedisTemplate.opsForValue().set(RedisKeyConstant.SHOP_PHONE, phone);
        return Result.success();
    }

    /**
     * 查询店铺联系电话
     *
     * @return 店铺联系电话
     */
    @GetMapping("/phone")
    @ApiOperation("查询店铺联系电话")
    public Result<String> getPhone() {
        String phone = stringRedisTemplate.opsForValue().get(RedisKeyConstant.SHOP_PHONE);
        if (phone == null || phone.isEmpty()) {
            phone = "400-618-4000";
        }
        log.info("查询店铺联系电话为：{}", phone);
        return Result.success(phone);
    }
}
