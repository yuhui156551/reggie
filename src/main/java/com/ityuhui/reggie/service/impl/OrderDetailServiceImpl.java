package com.ityuhui.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityuhui.reggie.entity.OrderDetail;
import com.ityuhui.reggie.mapper.OrderDetailMapper;
import com.ityuhui.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
