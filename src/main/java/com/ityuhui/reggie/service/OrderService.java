package com.ityuhui.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ityuhui.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 下单
     * @param orders
     */
    public void submit(Orders orders);
}
