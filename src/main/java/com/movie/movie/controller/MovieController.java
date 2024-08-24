package com.movie.movie.controller;

import com.movie.common.dto.CommonErrorDto;
import com.movie.common.dto.CommonResDto;
import com.movie.movie.domain.Movie;
import com.movie.movie.domain.RunningMovie;
import com.movie.movie.dto.MovieListDto;
import com.movie.movie.dto.RegisterMovieDto;
import com.movie.movie.dto.RegisterRunningMovieDto;
import com.movie.movie.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * 영화 등록 - admin 전용
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/regist/movie")
    public ResponseEntity<?> registerMovie(RegisterMovieDto dto) {

        try {
            Movie movie = movieService.registerMovie(dto);

            CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "등록이 완료되었습니다.", movie.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 상영 예정인 영화 등록(구체적인 상영 날짜와 함께) - admin전용
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/regist/runningMovie")
    public ResponseEntity<?> registerRunningMovie(RegisterRunningMovieDto dto) {

        try {
            RunningMovie runningMovie = movieService.registerRunningMovie(dto);

            CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "등록이 완료되었습니다.", runningMovie.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 상영중인 모든 영화 조회
     */
    @GetMapping("/findAllMovie")
    public ResponseEntity<?> findMovieAll() {

        try {
            List<MovieListDto> list = movieService.findMovieAll();

            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "전체 영화 조회 완료", list);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }
}
