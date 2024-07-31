package com.movie.movie.controller;

import com.movie.movie.service.ReservedMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
public class MovieReservationController {

    private final ReservedMovieService reservedMovieService;

    @Autowired
    public MovieReservationController(ReservedMovieService reservedMovieService) {
        this.reservedMovieService = reservedMovieService;
    }

}
