package com.movie.movie.dto;

import com.movie.movie.domain.Genre;
import com.movie.movie.domain.Movie;
import com.movie.movie.domain.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRunningMovieDto {

    private String screeningDate;
    private Option option; // 이벤트 영화관에서 상영하는 이벤트용 영화인지
    private Integer price; // 기본 14000원, 이벤트 영화 9000원
    private Long theaterId; // 상영관 번호 (1관, 2관, 3관)
    private Long movieId;

}
