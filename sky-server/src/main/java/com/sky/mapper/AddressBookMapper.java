package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 新增地址簿
     * @param addressBook
     */
    @Insert("insert into address_book (user_id, consignee, phone, sex, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default)" +
            "values (#{userId}, #{consignee}, #{phone}, #{sex}, #{provinceCode}, #{provinceName}, #{cityCode}, #{cityName}, #{districtCode}, #{districtName}, #{detail}, #{label}, #{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 修改地址簿
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 将当前用户的所有地址改为非默认地址
     * @param userId
     */
    @Update("update address_book set is_default = 0 where user_id = #{userId}")
    void updateIsDefaultByUserId(Long userId);

    /**
     * 动态条件查询地址簿
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 根据id删除地址簿
     * @param id
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id查询地址簿
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 查询当前用户的默认地址
     * @param addressBook
     * @return
     */
    @Select("select * from address_book where user_id = #{userId} and is_default = 1")
    AddressBook getDefault(AddressBook addressBook);
}
