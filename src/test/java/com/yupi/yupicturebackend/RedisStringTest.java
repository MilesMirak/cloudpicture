package com.yupi.yupicturebackend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RedisStringTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void testString() {
        ValueOperations<String,String> valueOperations=stringRedisTemplate.opsForValue();
        String key="testKey";
        String value="testValue";
        //增
        valueOperations.set(key,value);
        String result=valueOperations.get(key);
        assertEquals(value,result,"与预期不一致");
        //改
        valueOperations.set(key,"newValue");
        result=valueOperations.get(key);
        assertEquals("newValue",result);
        //查
        result=valueOperations.get(key);
        assertEquals("newValue",result);
        //删
        stringRedisTemplate.delete(key);
        result=valueOperations.get(key);
        assertEquals(null,result);
    }
}
