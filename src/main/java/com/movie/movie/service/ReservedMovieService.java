package com.movie.movie.service;

import com.movie.movie.repository.ReservedMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservedMovieService {

    private final ReservedMovieRepository reservedMovieRepository;

    @Autowired
    public ReservedMovieService(ReservedMovieRepository reservedMovieRepository) {
        this.reservedMovieRepository = reservedMovieRepository;
    }
}
