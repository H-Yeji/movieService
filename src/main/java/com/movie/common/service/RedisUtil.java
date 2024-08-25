package com.movie.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // 저장된 데이터 얼마나 유지할지 결정
    public void setExpire(String key, Object value, Long duration) {

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Duration expireDuratoin = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuratoin);
    }


    // redis에 value 객체를 담기 위한 설정
    public <T> T getData(String key, Class<T> clazz) { // redis에서 가져올 key, 반환될 데이터 타입 나타내는 class객체

        // redisTemplate : 이 객체는 redis와의 상호작용을 처리해주는 주요 객체. spring data redis에서 제공
        // opsForValue() : redis의 string 데이터 타입에 대한 작업을 수행할 수 있는 valueOperations 객체 반환
        // 이 객체를 통해 redis에서 값을 가져오거나 저장할 수 있음
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object value = valueOperations.get(key); //redis에셔 값을 가져와 object로 변환
        if (value != null) {
            return clazz.cast(value); // null이 아니면 그 값을 Class<T>로 변환
        }
        return null;
    }

}
