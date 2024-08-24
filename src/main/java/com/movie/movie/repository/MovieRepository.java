package com.movie.movie.repository;

import com.movie.movie.domain.Movie;
import com.movie.movie.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

//    Optional<Movie> findByTitleAndScreeningDate(String title, LocalDateTime screeningDate); // 제목 unique
    Optional<Movie> findByTitle(String title);
    Optional<Movie> findByMovieNum(String movieNum);

}
