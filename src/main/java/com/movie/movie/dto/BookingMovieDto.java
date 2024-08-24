package com.movie.movie.dto;

import com.movie.member.domain.Member;
import com.movie.movie.domain.BookedMovie;
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
public class BookingMovieDto {

    private String title;
    private String screeningDate;
    private String seatId;

    public BookedMovie toEntity(Member member, RunningMovie movie) {
        BookedMovie bookedMovie = BookedMovie.builder()
                .member(member)
                .runningMovie(movie)
                .build();
        return bookedMovie;
    }
}
