package com.movie.movie.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TheaterInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id이자 상영관 번호

    @Column(nullable = false)
    private int remainSeat; // 남은 좌석수

    /**
     * 데이터
     * id(관) 좌석수
     * 1      50
     * 2      50
     * 3      10
     */

    public void updateRemainSeat(int cnt) {
        this.remainSeat = this.remainSeat - cnt;
    }
}
