package com.ityuhui.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ityuhui.reggie.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
