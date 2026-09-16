package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 减少购物车中商品数量
     * @param shoppingCartDTO
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看当前用户的购物车数据
     * @return
     */
    List<ShoppingCart> showShoppingCartList();

    /**
     * 清空当前用户的购物车
     */
    void cleanShoppingCart();
}
