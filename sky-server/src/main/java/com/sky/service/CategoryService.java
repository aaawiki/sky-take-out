package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;


public interface CategoryService {

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void change(CategoryDTO categoryDTO);



    void startOrStop(Integer status, long id);

    void add(CategoryDTO categoryDTO);

    void delete(Integer id);

    List<Category> list(Integer type);
}
