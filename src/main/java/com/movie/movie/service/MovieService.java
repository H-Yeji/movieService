package com.movie.movie.service;

import com.movie.common.service.StockInventoryService;
import com.movie.movie.domain.Movie;
import com.movie.movie.domain.Option;
import com.movie.movie.domain.RunningMovie;
import com.movie.movie.domain.TheaterInfo;
import com.movie.movie.dto.MovieListDto;
import com.movie.movie.dto.RegisterMovieDto;
import com.movie.movie.dto.RegisterRunningMovieDto;
import com.movie.movie.repository.MovieRepository;
import com.movie.movie.repository.RunningMovieRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final TheaterInfoRepository theaterInfoRepository;
    private final StockInventoryService stockInventoryService;
    private final RunningMovieRepository runningMovieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository, TheaterInfoRepository theaterInfoRepository, StockInventoryService stockInventoryService, RunningMovieRepository runningMovieRepository) {
        this.movieRepository = movieRepository;
        this.theaterInfoRepository = theaterInfoRepository;
        this.stockInventoryService = stockInventoryService;
        this.runningMovieRepository = runningMovieRepository;
    }

    /**
     * 영화 등록 - admin 전용
     */
    @Transactional
    public Movie registerMovie(RegisterMovieDto dto) {

        MultipartFile image = dto.getPoster(); // 이미지 받아오기
        Movie movie = null;
        try {
            movie = movieRepository.save(dto.toEntity());

            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:/Users/yeji9/Yeji/movie_poster/"
                    , movie.getId()+ "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            movie.updateImagePath(path.toString());

        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        } catch (Exception e) {
            log.error("영화 등록 실패", e);
            throw e;
        }
        return movie;
    }


    /**
     * 상영할 영화 등록(구체적인 내용) - admin 전용
     */
    @Transactional
    public RunningMovie registerRunningMovie(RegisterRunningMovieDto dto) {

        RunningMovie runningMovie = null;
        try {
            Movie movie = movieRepository.findById(dto.getMovieId()).orElseThrow(
                    () -> new EntityNotFoundException("movie num과 일치하는 영화가 없습니다.")
            );
            // 영화 정보 등록
            Long theaterId = dto.getTheaterId(); // 상영관 번호
            TheaterInfo theaterInfo = theaterInfoRepository.findById(theaterId).orElseThrow(
                    () -> new EntityNotFoundException("해당id의 상영관이 존재하지 않습니다.")
            );

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dto.getScreeningDate(), dateTimeFormatter);


            // redis 이용
            if (dto.getOption().equals(Option.Event)) {
                stockInventoryService.initializeSeats(theaterId, 5, 10);
            }

            // 상영중인 영화 db에 등록
             runningMovie = RunningMovie.builder()
                    .movie(movie)
                    .theaterInfo(theaterInfo)
                    .remainSeat(theaterInfo.getSeat())
                    .screeningDate(dateTime)
                    .price(dto.getPrice())
                    .option(dto.getOption())
                    .build();
            runningMovieRepository.save(runningMovie);

        } catch (Exception e) {
            log.error("상영 예정 영화 등록에 실패: ", e.getMessage());
            throw e;
        }
        return runningMovie;
    }

    /**
     * 상영중인 모든 영화 조회
     */
    public List<MovieListDto> findMovieAll() {

        List<RunningMovie> movieList = runningMovieRepository.findAll();
        List<MovieListDto> dtos = new ArrayList<>();

        for (RunningMovie m: movieList) {
            dtos.add(m.listFromEntity());
        }
        return dtos;
    }

}
