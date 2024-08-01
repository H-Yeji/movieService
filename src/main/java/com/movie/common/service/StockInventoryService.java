package com.movie.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StockInventoryService {

    @Qualifier("2")
    private RedisTemplate<String, Object> redisTemplate;

    public StockInventoryService( @Qualifier("2") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 각 좌석마다 예약됨 / 사용 가능
    private static final String AVAILABLE = "available";
    private static final String RESERVED = "reserved";

    // 3관 좌석 세팅 (초기화)
    public void initializeSeats(Long theaterId, int rowCount, int seatsPerRow) {
        String theaterKey = "theater:" + theaterId; // 상영관 번호
        Map<String, String> seats = new HashMap<>();
        for (char row = 'A'; row < 'A' + rowCount; row++) { // 좌석 세팅
            for (int seat = 1; seat <= seatsPerRow; seat++) {
                String seatId = row + String.valueOf(seat);
                seats.put(seatId, AVAILABLE); // 모두 AVAILABLE로 초기화
                log.info("좌석 초기화 - {}관 {}석 상태: {}", theaterKey, seatId, AVAILABLE);
            }
        }
        redisTemplate.opsForHash().putAll(theaterKey, seats);
    }

    public boolean reserveSeat(Long theaterId, String seatId) {
        String theaterKey = "theater:" + theaterId;
        String seatStatus = (String) redisTemplate.opsForHash().get(theaterKey, seatId); // ex ) 1관 B2석
        log.info("theaterKey: {}, seatStatus: {}", theaterKey, seatStatus);
        if (AVAILABLE.equals(seatStatus)) { // 해당 좌석이 이용 가능한 상태면
            redisTemplate.opsForHash().put(theaterKey, seatId, RESERVED); // 예약됨으로 상태 변경
            return true;
        }
        return false;
    }


//    // 좌석 수 증가
//    public Long increaseStock(Long movieId, int seat) {
//        log.info("movieId: {}, seat: {}", movieId, seat);
//        return redisTemplate.opsForValue().increment(String.valueOf(movieId), seat);
//    }
//
//    // 좌석 수 감소
//    public Long decreaseStock(Long movieId, int seat) {
////        Object getSeat = redisTemplate.opsForValue().get(String.valueOf(movieId));
//        String key = String.valueOf(movieId);
//        Object getSeat = redisTemplate.opsForValue().get(key);
//        if (getSeat == null) {
//            throw new IllegalArgumentException("해당 영화가 없습니다.");
//        }
//        int remainSeat = Integer.parseInt(getSeat.toString());
//
//        // 꺼내온 remainSeat과 비교
//        if (remainSeat < seat) {
//            return -1L;
//        } else {
////            return redisTemplate.opsForValue().decrement(String.valueOf(movieId));
//            // 예매가 들어온 좌석 수 만큼 redis에서 감소시키기 (1씩 말고)
//             return redisTemplate.execute((RedisCallback<Long>) connection -> connection.decrBy(key.getBytes(), seat));
//        }
//    }
}
