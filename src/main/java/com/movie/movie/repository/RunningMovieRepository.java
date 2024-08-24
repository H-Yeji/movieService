package com.movie.movie.repository;

import com.movie.movie.domain.Movie;
import com.movie.movie.domain.RunningMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RunningMovieRepository extends JpaRepository<RunningMovie, Long> {

    RunningMovie findByMovie(Movie movie);
    RunningMovie findByMovieAndScreeningDate(Movie movie, LocalDateTime screeningDate);
}
