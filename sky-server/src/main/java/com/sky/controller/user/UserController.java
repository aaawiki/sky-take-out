package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.dto.WebUserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户端-用户相关接口
 */
@RestController
@RequestMapping("/user/user")
@Api(tags = "用户端-用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信小程序登录
     *
     * @param userLoginDTO 携带微信登录授权码code
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信用户登录，授权码：{}", userLoginDTO.getCode());

        //微信登录
        User user = userService.wxLogin(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .avatar(user.getAvatar())
                .build();
        return Result.success(userLoginVO);
    }

    /**
     * 网页端邮箱密码登录
     *
     * @param webUserLoginDTO 邮箱与密码
     * @return
     */
    @PostMapping("/webLogin")
    @ApiOperation("网页端邮箱密码登录")
    public Result<UserLoginVO> webLogin(@RequestBody WebUserLoginDTO webUserLoginDTO) {
        log.info("网页端用户登录请求，邮箱：{}", webUserLoginDTO.getEmail());

        User user = userService.webLogin(webUserLoginDTO);

        // 登录成功后，生成 jwt 令牌 (带有用户 ID)
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .email(user.getEmail())
                .name(user.getName() != null ? user.getName() : "用户" + user.getId())
                .avatar(user.getAvatar())
                .build();
        return Result.success(userLoginVO);
    }

    /**
     * 网页端邮箱注册并登录
     *
     * @param webUserLoginDTO 邮箱与密码
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("网页端邮箱注册")
    public Result<UserLoginVO> register(@RequestBody WebUserLoginDTO webUserLoginDTO) {
        log.info("网页端用户注册请求，邮箱：{}", webUserLoginDTO.getEmail());

        User user = userService.webRegister(webUserLoginDTO);

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .email(user.getEmail())
                .name(user.getName() != null ? user.getName() : "用户" + user.getId())
                .avatar(user.getAvatar())
                .build();
        return Result.success(userLoginVO);
    }
}
