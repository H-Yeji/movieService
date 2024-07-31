package com.movie.movie.repository;

import com.movie.movie.domain.Movie;
import com.movie.movie.domain.ReservedMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservedMovieRepository extends JpaRepository<ReservedMovie, Long> {
}
