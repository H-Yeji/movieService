package com.movie.movie.dto;

import com.movie.movie.domain.Genre;
import com.movie.movie.domain.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterMovieDto {

    private Long id;

    private String movieNum; // 영화 고유의 번호

    private String title;

    private Genre genre; // 장르

    private MultipartFile poster;

    public Movie toEntity() {
        Movie movie = Movie.builder()
                .id(this.id)
                .movieNum(this.movieNum)
                .title(this.title)
                .genre(this.genre)
                .imagePath(String.valueOf(this.poster))
                .build();
        return movie;
    }
}
