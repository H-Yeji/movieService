package com.movie.movie.service;

import com.movie.common.service.StockInventoryService;
import com.movie.movie.domain.Movie;
import com.movie.movie.domain.Option;
import com.movie.movie.domain.TheaterInfo;
import com.movie.movie.dto.MovieRegistDto;
import com.movie.movie.repository.MovieRepository;
import com.movie.movie.repository.TheaterInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional(readOnly = true)
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final TheaterInfoRepository theaterInfoRepository;
    private final StockInventoryService stockInventoryService;

    @Autowired
    public MovieService(MovieRepository movieRepository, TheaterInfoRepository theaterInfoRepository, StockInventoryService stockInventoryService) {
        this.movieRepository = movieRepository;
        this.theaterInfoRepository = theaterInfoRepository;
        this.stockInventoryService = stockInventoryService;
    }

    /**
     * 영화 등록 - admin 전용
     */
    @Transactional
    public Movie registMovie(MovieRegistDto dto) {

        MultipartFile image = dto.getPoster(); // 이미지 받아오기
        Movie movie = null;
        try {
            Long theaterId = dto.getTheaterId(); // 상영관 번호
            TheaterInfo theaterInfo = theaterInfoRepository.findById(theaterId).orElseThrow(
                    () -> new EntityNotFoundException("해당id의 상영관이 존재하지 않습니다.")
            );

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dto.getScreeningDate(), dateTimeFormatter);

            movie = movieRepository.save(dto.toEntity(theaterInfo, dateTime));

            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:/Users/Playtdata/Desktop/tmp/"
                    , movie.getId()+ "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            movie.updateImagePath(path.toString());

            // redis 이용
            if (dto.getOption().equals(Option.Event)) {
                stockInventoryService.initializeSeats(theaterId, 2, 5); //좌석 세팅 (A1,2,3,4,5 / B1,2,3,4,5)
            }

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        } catch (Exception e) {
            log.error("Error registering movie", e);
            throw e;
        }
        return movie;
    }
}
