package com.ityuhui.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ityuhui.reggie.common.R;
import com.ityuhui.reggie.entity.User;
import com.ityuhui.reggie.service.UserService;
import com.ityuhui.reggie.utils.SMSUtils;
import com.ityuhui.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> senMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code-->{}",code); //搞不到签名，只能这样从后台偷偷看验证码了哈哈
            //调用阿里云API发送短信
            //SMSUtils.sendMessage();
            //将生成的验证码保存
            //session.setAttribute(phone,code); //phone为键，code为值存入

            //将生成的验证码缓存到Redis中，设置有效期为30s（方便测试）
            redisTemplate.opsForValue().set(phone,code,30, TimeUnit.SECONDS);

            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败");
    }

    /**
     * 登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info("user-->{}",map);

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //获取session保存的验证码
        //Object codeInSession = session.getAttribute(phone);

        //从Redis获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        //比对
        if(codeInSession != null && codeInSession.equals(code)){
            //判断是否是新用户，若是，则自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            if(user == null){
                //说明数据库没有，自动注册
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());//存session一份，防止过滤器拦截

            //如果登陆成功，删除Redis中缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登陆失败");
    }
}
