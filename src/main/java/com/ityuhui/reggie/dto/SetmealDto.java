package com.ityuhui.reggie.dto;

import com.ityuhui.reggie.entity.Setmeal;
import com.ityuhui.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
