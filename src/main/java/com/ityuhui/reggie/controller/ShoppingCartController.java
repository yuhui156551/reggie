package com.ityuhui.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ityuhui.reggie.common.BaseContext;
import com.ityuhui.reggie.common.R;
import com.ityuhui.reggie.entity.ShoppingCart;
import com.ityuhui.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 新增菜品或套餐
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据：{}", shoppingCart);

        //用户id页面没有传过来，需要自己设置
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或套餐是否存在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //说明此时添加的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            //此时添加的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        }

        //SQL:select * from shopping_cart where user_id = ? and dish_id = ?或者是
        //SQL:select * from shopping_cart where user_id = ? and setmeal_id = ?
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        if (one != null) {
            //已存在，数量+1，执行更新操作
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            one.setCreateTime(LocalDateTime.now());
            shoppingCartService.updateById(one);
        } else {
            //执行insert操作
            shoppingCart.setNumber(1);//第一次入库，设置成1
            shoppingCartService.save(shoppingCart);
            one = shoppingCart; //考虑到没有执行else的情况
        }

        return R.success(one);
    }

    /**
     * 减少菜品或套餐  照猫画虎，easy
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据：{}", shoppingCart);

        //用户id页面没有传过来，需要自己设置
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或套餐是否存在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            //说明此时减少的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            //此时减少的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        }

        //SQL:select * from shopping_cart where user_id = ? and dish_id = ?或者是
        //SQL:select * from shopping_cart where user_id = ? and setmeal_id = ?
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);

        //已存在，数量-1，执行更新操作
        Integer number = one.getNumber();
        one.setNumber(number - 1);
        shoppingCartService.updateById(one);


        return R.success(one);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
