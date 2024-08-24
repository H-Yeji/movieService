package com.movie.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieListDto { // 상영중인 전체 영화 리스트

    private Long id;
    private String title;
    private String runningDateTime; // 상영 날짜 및 시간
    private Long remainSeat; // 남은 좌석수
}
