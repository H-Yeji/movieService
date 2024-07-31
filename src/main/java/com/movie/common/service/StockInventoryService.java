package com.movie.common.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class StockInventoryService {

    @Qualifier("2")
    private RedisTemplate<String, Object> redisTemplate;

    public StockInventoryService( @Qualifier("2") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 좌석 수 증가
    public Long increaseStock(Long movieId, Long cnt) {
        return redisTemplate.opsForValue().increment(String.valueOf(movieId), cnt);
    }
}
