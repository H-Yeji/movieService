package com.movie.movie.service;

import com.movie.common.service.StockInventoryService;
import com.movie.member.domain.Member;
import com.movie.member.repository.MemberRepository;
import com.movie.movie.domain.*;
import com.movie.movie.dto.BookingMovieDto;
import com.movie.movie.dto.BookedMovieListDto;
import com.movie.movie.dto.MyMovieListDto;
import com.movie.movie.repository.BookedMovieRepository;
import com.movie.movie.repository.MovieRepository;
import com.movie.movie.repository.RunningMovieRepository;
import com.movie.movie.repository.TheaterInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(readOnly = true)
@Slf4j
public class BookingMovieService {

    private final BookedMovieRepository bookedMovieRepository;
    private final MemberRepository memberRepository;
    private final MovieRepository movieRepository;
    private final TheaterInfoRepository theaterInfoRepository;
    private final StockInventoryService  stockInventoryService;
    private final RunningMovieRepository runningMovieRepository;

    @Autowired
    public BookingMovieService(BookedMovieRepository bookedMovieRepository, MemberRepository memberRepository, MovieRepository movieRepository, TheaterInfoRepository theaterInfoRepository, StockInventoryService stockInventoryService, RunningMovieRepository runningMovieRepository) {
        this.bookedMovieRepository = bookedMovieRepository;
        this.memberRepository = memberRepository;
        this.movieRepository = movieRepository;
        this.theaterInfoRepository = theaterInfoRepository;
        this.stockInventoryService = stockInventoryService;
        this.runningMovieRepository = runningMovieRepository;
    }

    /**
     * 영화 예매
     */
    @Transactional
    public BookedMovie bookingMovie(BookingMovieDto dto) {

        // 로그인 한 사람 -> user
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new EntityNotFoundException("해당 email의 회원은 존재하지 않습니다.")
        );

        Movie movie = movieRepository.findByTitle(dto.getTitle()).orElseThrow(
                () -> new EntityNotFoundException("해당 제목의 영화가 없습니다.")
        );

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dto.getScreeningDate(), dateTimeFormatter);

        RunningMovie runningMovie = runningMovieRepository.findByMovieAndScreeningDate(movie, dateTime);

        // 영화 예매
        BookedMovie bookedMovie = dto.toEntity(member, runningMovie);
//        if (bookedMovie.getMovie().getOption().equals(Option.Event)) { // 이벤트 영화이면
//            // redis
//            boolean success = stockInventoryService.reserveSeat(movie.getTheaterInfo().getId(), dto.getSeatId());
//            log.info("2 success : " + success);
//            if (!success) {
//                throw new IllegalArgumentException(dto.getSeatId() +"좌석은 선택할 수 없습니다.");
//            }
//            log.info("3");
//        } else { // 이벤트 용 영화가 아니면
            // 예매 성공하면 -> 좌석 수 인원수만큼 차감
            TheaterInfo theater = theaterInfoRepository.findById(movie.getId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 id의 상영관이 존재하지 않습니다.")
            );

//            RunningMovie findMovie = runningMovieRepository.findByMovie(movie);
            runningMovie.updateRemainSeat(1); // 한자리씩 예약 가능
            log.info("{}번 id영화 남은 좌석 수: {}", runningMovie.getId(), runningMovie.getRemainSeat());
//        }

        return bookedMovieRepository.save(bookedMovie);
    }

    /**
     *  본인 영화 얘매 내역 조회
     */
    public Page<MyMovieListDto> myMovieList(Pageable pageable) {

        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new EntityNotFoundException("해당 email의 회원은 존재하지 않습니다.")
        );

        Page<BookedMovie> bookings = bookedMovieRepository.findByMember(pageable, member);
        AtomicInteger start = new AtomicInteger((int) pageable.getOffset());

        Page<MyMovieListDto> bookingDtos = bookings.map(a->a.listFromEntity(start.incrementAndGet()));

        return bookingDtos;
    }

    /**
     * 영화 예매 내역 전체 조회 - admin 전용
     */
    public Page<BookedMovieListDto> movieList(Pageable pageable) {

        Page<BookedMovie> list = bookedMovieRepository.findAll(pageable);

        AtomicInteger start = new AtomicInteger((int) pageable.getOffset());

        Page<BookedMovieListDto> listDtos = list.map(a -> a.listAllFromEntity(start.incrementAndGet()));

        return listDtos;
    }

}
