package com.ityuhui.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ityuhui.reggie.common.R;
import com.ityuhui.reggie.dto.DishDto;
import com.ityuhui.reggie.entity.Category;
import com.ityuhui.reggie.entity.Dish;
import com.ityuhui.reggie.entity.DishFlavor;
import com.ityuhui.reggie.entity.Setmeal;
import com.ityuhui.reggie.service.CategoryService;
import com.ityuhui.reggie.service.DishFlavorService;
import com.ityuhui.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")  //<--这一段需要多看几遍，还有前端代码也很牛--2022-11-9-0:22。
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {},pageSize = {},name = {}", page, pageSize, name);
        //分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime); //根据更新时间降序排序
        //执行
        dishService.page(pageInfo,queryWrapper);

        /*此时有个问题，Dish只含有categoryId属性，前端只接受categoryName属性*/
        //对象拷贝,除去records属性,  records-->列表数据所承载的集合
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);//dishDto是新new出来的，属性都是空的

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);//查数据库
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());//收集

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品和口味信息
     * @param id
     * @return
     */
    //Request URL: http://localhost:8080/dish/1589963644337135618
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    /*@GetMapping("/list")
    //Request URL: http://localhost:8080/dish/list?categoryId=1397844263642378242
    public R<List<Dish>> list(Dish dish){  //Dish包含categoryId，通用性更强
        //查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //查询未停售的(起售)
        queryWrapper.eq(Dish::getStatus,1);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);
    }*/

    //客户端页面带口味的菜品需要显示口味信息
    @GetMapping("/list")
    //Request URL: http://localhost:8080/dish/list?categoryId=1397844263642378242
    public R<List<DishDto>> list(Dish dish){  //Dish包含categoryId，通用性更强
        //查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //查询未停售的(起售)
        queryWrapper.eq(Dish::getStatus,1);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);
        
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);//dishDto是新new出来的，属性都是空的

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);//查数据库
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishId = item.getId();//当前菜品id
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());//收集
        

        return R.success(dishDtoList);
    }


    /**
     * 自己写
     * 修改菜品出售状态，用Long[]表示包括批量处理
     * @param status
     * @param ids
     * @return
     */
    //Request URL: http://localhost:8080/dish/status/0?ids=1591688715351146498
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,Long[] ids){
        List<Long> list = Arrays.asList(ids);

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus,status).in(Dish::getId,list);

        dishService.update(updateWrapper);

        return R.success("修改信息成功");
    }

    /**
     * 删除菜品--自己写
     * @param ids
     * @return
     */
    //Request URL: http://localhost:8080/dish?ids=1591688715351146498
    @DeleteMapping
    //deleteDish(type === '批量' ? this.checkList.join(',') : id).then(res => {
    //使用String接收参数
    public R<String> delete(Long[] ids){
        //凡事先写好一点，边测试边写
        List<Long> list = Arrays.asList(ids);

        dishService.removeByIds(list);//执行批量删除

        log.info("删除的ids: {}",ids);

        return R.success("删除成功"); //返回成功
    }
}
