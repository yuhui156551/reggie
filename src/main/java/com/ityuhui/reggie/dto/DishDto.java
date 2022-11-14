package com.ityuhui.reggie.dto;

import com.ityuhui.reggie.entity.Dish;
import com.ityuhui.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data  //dto数据传输对象，用于数据层服务层数据传输
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
