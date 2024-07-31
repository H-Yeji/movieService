package com.movie.movie.repository;

import com.movie.movie.domain.TheaterInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterInfoRepository extends JpaRepository<TheaterInfo, Long> {
}
