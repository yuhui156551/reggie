package com.ityuhui.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ityuhui.reggie.entity.Category;


public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
