package com.ityuhui.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityuhui.reggie.entity.DishFlavor;
import com.ityuhui.reggie.mapper.DishFlavorMapper;
import com.ityuhui.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
