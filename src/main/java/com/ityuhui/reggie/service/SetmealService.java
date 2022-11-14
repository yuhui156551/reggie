package com.ityuhui.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ityuhui.reggie.dto.SetmealDto;
import com.ityuhui.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除套餐和菜品的关联数据
     * @param ids
     */
    public void deleteWithDish(List<Long> ids);

    SetmealDto getData(Long id);
}
