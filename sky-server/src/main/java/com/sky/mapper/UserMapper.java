package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据微信用户的openid查询用户
     * @param openid 微信用户的唯一标识
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 根据邮箱查询用户（网页端）
     * @param email 用户邮箱
     * @return
     */
    @Select("select * from user where email = #{email}")
    User getByEmail(String email);

    /**
     * 根据ID查询用户
     * @param id 用户主键
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 插入用户数据
     * @param user
     */
    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time, email, password)" +
            "values (#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime}, #{email}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    /**
     * 动态条件统计用户数量，用于统计报表中的新增用户数
     * @param map 可包含 begin、end 两个时间条件
     * @return
     */
    Integer countByMap(Map map);
}
