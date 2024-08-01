package com.movie.movie.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.movie.movie.domain.Genre;
import com.movie.movie.domain.Movie;
import com.movie.movie.domain.Option;
import com.movie.movie.domain.TheaterInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRegistDto {

    private String movieNum;
    private String title;
    private Genre genre;
    private String screeningDate;
    private Option option; // 이벤트 영화관에서 상영하는 이벤트용 영화인지
    private Integer price; // 기본 14000원, 이벤트 영화 9000원
    private MultipartFile poster; // 영화 포스터 사진
    private Long theaterId; // 상영관 번호 (1관, 2관, 3관)

    public Movie toEntity(TheaterInfo theaterInfo, LocalDateTime dateTime) {
        Movie movie = Movie.builder()
                .movieNum(this.movieNum)
                .title(this.title)
                .genre(this.genre)
                .screeningDate(dateTime)
                .price(this.price)
                .option(this.option)
                .theaterInfo(theaterInfo)
                .imagePath(String.valueOf(this.poster))
                .build();
        return movie;
    }
}
