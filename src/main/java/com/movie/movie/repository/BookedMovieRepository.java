package com.movie.movie.repository;

import com.movie.member.domain.Member;
import com.movie.movie.domain.BookedMovie;
import com.movie.movie.domain.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookedMovieRepository extends JpaRepository<BookedMovie, Long> {

    Page<BookedMovie> findByMember(Pageable pageable, Member member);
    Page<BookedMovie> findAll(Pageable pageable);
}
