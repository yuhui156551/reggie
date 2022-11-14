package com.ityuhui.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityuhui.reggie.common.CustomException;
import com.ityuhui.reggie.dto.SetmealDto;
import com.ityuhui.reggie.entity.Setmeal;
import com.ityuhui.reggie.entity.SetmealDish;
import com.ityuhui.reggie.mapper.SetmealMapper;
import com.ityuhui.reggie.service.SetmealDishService;
import com.ityuhui.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /*
    setmeal表-->套餐表，包含套餐分类id：category_id
    setmeal_dish表-->套餐菜品关联表，包含套餐id：setmeal_id还有菜品id：dish_id
    * */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setmeal
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐菜品关联信息，操作setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void deleteWithDish(List<Long> ids) {
        //停售状态才可以删除
        //查询套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = (int) this.count(queryWrapper);//调用的是ServiceImpl里面的方法
        if(count > 0){
            throw new CustomException("商品起售状态，无法删除");
        }
        //删除套餐表里的数据
        this.removeByIds(ids);
        //删除关联表里相关数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

    @Override
    public SetmealDto getData(Long id) {
        //因为前端给到我们的字段就是id，我们先根据这个id去查询具体的套餐
        Setmeal setmeal =this.getById(id);//根据id查询到套餐
        //构造扩展出来的setmealDto对象
        //最后我们会将所有的数据封装到setmealDto当中
        SetmealDto setmealDto = new SetmealDto();
        //构造菜品套餐关联的条件查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //根据套餐来查询菜品
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);//这里根据套餐id查询关联的菜品
        if (setmeal!=null)//查询到的套餐不是空
        {
//            拷贝一下数据
            BeanUtils.copyProperties(setmeal,setmealDto);//先将套餐的的数据字段拷贝到扩展的实体类
            //这里具体对关联的菜品进行了查询
            List<SetmealDish> list = setmealDishService.list(queryWrapper);//这是查询到的菜品数据

            setmealDto.setSetmealDishes(list);//将菜品数据传过去
            return setmealDto;
        }

        return null;
    }


}
