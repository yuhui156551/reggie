package com.ityuhui.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityuhui.reggie.entity.ShoppingCart;
import com.ityuhui.reggie.mapper.ShoppingCartMapper;
import com.ityuhui.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
