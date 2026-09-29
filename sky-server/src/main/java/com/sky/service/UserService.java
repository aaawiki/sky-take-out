package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.dto.WebUserLoginDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 网页端邮箱密码登录
     * @param webUserLoginDTO
     * @return
     */
    User webLogin(WebUserLoginDTO webUserLoginDTO);

    /**
     * 网页端邮箱注册
     * @param webUserLoginDTO
     * @return
     */
    User webRegister(WebUserLoginDTO webUserLoginDTO);
}
