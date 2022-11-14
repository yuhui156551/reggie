package com.ityuhui.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ityuhui.reggie.dto.DishDto;
import com.ityuhui.reggie.entity.Dish;


public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品和口味信息
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
