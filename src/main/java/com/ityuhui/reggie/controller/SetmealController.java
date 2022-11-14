package com.ityuhui.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ityuhui.reggie.common.R;
import com.ityuhui.reggie.dto.SetmealDto;
import com.ityuhui.reggie.entity.Category;
import com.ityuhui.reggie.entity.Setmeal;
import com.ityuhui.reggie.entity.SetmealDish;
import com.ityuhui.reggie.service.CategoryService;
import com.ityuhui.reggie.service.SetmealDishService;
import com.ityuhui.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime); //根据更新时间降序排序
        //执行
        setmealService.page(pageInfo, queryWrapper);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");//泛型不一样，不能拷过去
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {//千万记得@RequestParam！！
        log.info("id:{}", ids);

        setmealService.deleteWithDish(ids);

        return R.success("套餐数据删除成功");
    }

    /**
     * 修改出售状态-->自己写   0-->现在是起售状态，希望修改为禁售状态
     * 自己写的很失败，我测我也不知道会有lambdaupdatewrapper这种条件构造器，妈的这么好用
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status, Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        log.info("id-->", list);

        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>(); //TODO LambdaUpdateWrapper
        updateWrapper.set(Setmeal::getStatus,status).in(Setmeal::getId,list);
        setmealService.update(updateWrapper);

        return R.success("套餐信息修改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){ //参数是键值对形式
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

    /*这里主要做一个数据回显*/
    @GetMapping("/{id}")
    public R<SetmealDto> getData(@PathVariable Long id)
    {
        SetmealDto setmealDto = setmealService.getData(id);
        return  R.success(setmealDto);
    }


    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> edit(@RequestBody SetmealDto setmealDto){
        //先判断是否接收到数据
        if(setmealDto==null)
        {
            return  R.error("请求异常");
        }
        //判断套餐下面是否还有关联菜品
        if(setmealDto.getSetmealDishes()==null)
        {
            return R.error("套餐没有菜品，请添加");
        }
        //获取到关联的菜品列表，注意关联菜品的列表使我们提交过来的
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //获取到套餐的id
        Long setmealId = setmealDto.getId();
        //构造关联菜品的条件查询
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //根据套餐id在关联菜品中查询数据，注意这里所做的查询是在数据库中进行查询的
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        //关联菜品先移除掉原始数据库中的数据
        setmealDishService.remove(queryWrapper);
        //为setmeal_dish表填充相关的属性
        //这里我们需要为关联菜品的表前面的字段填充套餐的id
        for(SetmealDish setmealDish:setmealDishes)
        {
            setmealDish.setSetmealId(setmealId);//填充属性值
        }
        //批量把setmealDish保存到setmeal_dish表
        //这里我们保存了我们提交过来的关联菜品数据
        setmealDishService.saveBatch(setmealDishes);//保存套餐关联菜品
        //这里我们正常更新套餐
        setmealService.updateById(setmealDto);//保存套餐
        return R.success("套餐修改成功");
    }
}
