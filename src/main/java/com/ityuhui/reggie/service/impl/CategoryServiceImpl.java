package com.ityuhui.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityuhui.reggie.common.CustomException;
import com.ityuhui.reggie.entity.Category;
import com.ityuhui.reggie.entity.Dish;
import com.ityuhui.reggie.entity.Setmeal;
import com.ityuhui.reggie.mapper.CategoryMapper;
import com.ityuhui.reggie.service.CategoryService;
import com.ityuhui.reggie.service.DishService;
import com.ityuhui.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，需进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //sql语句：select count(*) from dish where category_id = ?;

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //构造查询条件,根据分类id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = (int) dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，若关联，抛出异常
        if(count1 > 0){
            //手动抛出异常
            throw new CustomException("当前分类下关联了菜品，无法删除!");
        }
        //查询当前分类是否关联了套餐，若关联，抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = (int) setmealService.count(setmealLambdaQueryWrapper);

        if(count2 > 0){
            throw new CustomException("当前分类下关联了套餐，无法删除!");
        }
        //正常删除分类
        super.removeById(id);
    }
}
