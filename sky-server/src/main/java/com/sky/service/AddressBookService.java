package com.sky.service;

import com.sky.entity.AddressBook;
import java.util.List;

public interface AddressBookService {

    /**
     * 新增地址簿
     * @param addressBook
     */
    void save(AddressBook addressBook);

    /**
     * 修改地址簿
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id删除地址簿
     * @param id
     */
    void delete(Long id);

    /**
     * 动态条件查询地址簿
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 根据id查询地址簿
     * @param id
     * @return
     */
    AddressBook getById(Long id);

    /**
     * 查询当前用户的默认地址
     * @param addressBook
     * @return
     */
    AddressBook getDefault(AddressBook addressBook);

    /**
     * 设置默认地址
     * @param addressBook
     */
    void setDefault(AddressBook addressBook);
}
