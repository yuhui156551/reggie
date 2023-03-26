package com.ityuhui.reggie;

import com.ityuhui.reggie.ReggieApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;


import javax.annotation.Resource;

@SpringBootTest
class ReggieApplicationTests {

    /**
     * redis连接测试
     */
    @Test
    void testConnect() {
        Jedis jedis = new Jedis("192.168.44.100", 6379);
        jedis.auth("123321");
        String ping = jedis.ping();
        System.out.println(ping);
    }

}
