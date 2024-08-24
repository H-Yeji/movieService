package com.movie.movie.dto;

import com.movie.movie.domain.Movie;
import com.movie.movie.domain.RunningMovie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookedMovieListDto { // 예매된 영화 리스트 (admin 전용)

    private int no;
    private String memberEmail;
    private RunningMovie runningMovie;
}
