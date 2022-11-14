package com.ityuhui.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ityuhui.reggie.mapper.UserMapper;
import com.ityuhui.reggie.service.UserService;
import com.ityuhui.reggie.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {
}
