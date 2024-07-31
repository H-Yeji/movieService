package com.movie.movie.controller;

import com.movie.common.dto.CommonErrorDto;
import com.movie.common.dto.CommonResDto;
import com.movie.movie.domain.Movie;
import com.movie.movie.dto.MovieRegistDto;
import com.movie.movie.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;

@RestController
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * 상영 예정 영화 등록 - admin전용
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/regist/movie")
    public ResponseEntity<?> registMovie(MovieRegistDto dto) {

        try {
            log.info("controller전");
            Movie movie = movieService.registMovie(dto);
            log.info("controller후");
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "등록이 완료되었습니다.", movie.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 테스트용
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public String hello() {
        return "ok";
    }
}
