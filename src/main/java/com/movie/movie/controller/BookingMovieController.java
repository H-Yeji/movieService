package com.movie.movie.controller;

import com.movie.common.dto.CommonErrorDto;
import com.movie.common.dto.CommonResDto;
import com.movie.movie.domain.BookedMovie;
import com.movie.movie.dto.MovieBookDto;
import com.movie.movie.dto.MovieListDto;
import com.movie.movie.dto.MyMovieListDto;
import com.movie.movie.service.BookingMovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/movie")
public class BookingMovieController {

    private static final Logger log = LoggerFactory.getLogger(BookingMovieController.class);
    private final BookingMovieService bookingMovieService;

    @Autowired
    public BookingMovieController(BookingMovieService bookingMovieService) {
        this.bookingMovieService = bookingMovieService;
    }

    /**
     *  영화 예매
     */
    @PostMapping("/book")
    public ResponseEntity<?> bookingMovie(@RequestBody MovieBookDto dto) {

        try {
            BookedMovie bookedMovie = bookingMovieService.bookingMovie(dto);

            CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "예매 성공", bookedMovie.getMovie());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 본인의 영화 예매 내역 조회
     */
    @GetMapping("/list")
    public Page<MyMovieListDto> myMovieList(Pageable pageable) {

        return bookingMovieService.myMovieList(pageable);
    }

    /**
     * 예매 목록 조회 - admin 전용
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listAll")
    public Page<MovieListDto> movieList(Pageable pageable) {

        return bookingMovieService.movieList(pageable);
    }

}
