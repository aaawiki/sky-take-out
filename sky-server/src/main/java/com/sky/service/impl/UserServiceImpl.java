package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.WebUserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BaseException;
import com.sky.exception.LoginFailedException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 微信登录
     *
     * @param userLoginDTO 携带微信登录授权码code
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //1、调用微信接口服务，获得当前微信用户的openid
        String openid = getOpenid(userLoginDTO.getCode());

        //2、判断openid是否为空，如果为空表示登录失败
        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //3、根据openid判断当前用户是否为新用户，如果是新用户，自动完成注册
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //4、返回用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     *
     * @param code 微信登录授权码
     * @return
     */
    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        log.info("微信登录接口返回：{}", json);
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }

    /**
     * 网页端邮箱密码登录
     *
     * @param webUserLoginDTO 邮箱与密码
     * @return User
     */
    @Override
    public User webLogin(WebUserLoginDTO webUserLoginDTO) {
        String email = webUserLoginDTO.getEmail();
        String password = webUserLoginDTO.getPassword();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new BaseException("邮箱或密码不能为空");
        }

        email = email.trim();
        User user = userMapper.getByEmail(email);

        if (user == null) {
            throw new AccountNotFoundException("该邮箱未注册，请先注册账号");
        }

        // 密码 MD5 比对
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if (user.getPassword() == null || !user.getPassword().equalsIgnoreCase(md5Password)) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        log.info("网页端用户登录成功，用户ID：{}，邮箱：{}", user.getId(), user.getEmail());
        return user;
    }

    /**
     * 网页端邮箱注册
     *
     * @param webUserLoginDTO 邮箱与密码
     * @return User
     */
    @Override
    public User webRegister(WebUserLoginDTO webUserLoginDTO) {
        String email = webUserLoginDTO.getEmail();
        String password = webUserLoginDTO.getPassword();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new BaseException("邮箱或密码不能为空");
        }

        email = email.trim();
        User existingUser = userMapper.getByEmail(email);
        if (existingUser != null) {
            throw new BaseException("该邮箱已被注册，请直接登录");
        }

        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        String defaultName = email.contains("@") ? email.split("@")[0] : email;

        User user = User.builder()
                .email(email)
                .password(md5Password)
                .name(defaultName)
                .avatar("/static/boy.png")
                .createTime(LocalDateTime.now())
                .build();

        userMapper.insert(user);
        log.info("网页端新用户注册成功，用户ID：{}，邮箱：{}", user.getId(), user.getEmail());
        return user;
    }
}
