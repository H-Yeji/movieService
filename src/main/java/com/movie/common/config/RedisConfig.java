package com.movie.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    public String host;

    @Value("${spring.redis.port}")
    public int port;

    @Bean
    @Qualifier("2")
    public RedisConnectionFactory redisConnectionFactory() {
        // connection 정보 넣기
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        // 1번 db 사용 - redis에서 select 1로 확인
        configuration.setDatabase(1);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("2")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("2") RedisConnectionFactory redisConnectionFactory){

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // string 형태를 직렬화 시키게따 (java에 string으로 들어가게)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 제이슨 직렬화 툴 세팅
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 주입받은 connection을 넣어줌
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
