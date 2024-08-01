package com.movie.movie.dto;

import com.movie.member.domain.Member;
import com.movie.movie.domain.BookedMovie;
import com.movie.movie.domain.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieBookDto {

    private String title;
    private String screeningDate;
    private String seatId; //A1, A2,,
//    private int cnt; // 인원수

    public BookedMovie toEntity(Member member, Movie movie) {
        BookedMovie bookedMovie = BookedMovie.builder()
                .member(member)
                .movie(movie)
                .build();
        return bookedMovie;
    }
}
