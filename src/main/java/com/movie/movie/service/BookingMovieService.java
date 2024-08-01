package com.movie.movie.service;

import com.movie.common.service.StockInventoryService;
import com.movie.member.domain.Member;
import com.movie.member.repository.MemberRepository;
import com.movie.movie.domain.BookedMovie;
import com.movie.movie.domain.Movie;
import com.movie.movie.domain.Option;
import com.movie.movie.domain.TheaterInfo;
import com.movie.movie.dto.MovieBookDto;
import com.movie.movie.dto.MovieListDto;
import com.movie.movie.dto.MyMovieListDto;
import com.movie.movie.repository.BookedMovieRepository;
import com.movie.movie.repository.MovieRepository;
import com.movie.movie.repository.TheaterInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    public BookingMovieService(BookedMovieRepository bookedMovieRepository, MemberRepository memberRepository, MovieRepository movieRepository, TheaterInfoRepository theaterInfoRepository, StockInventoryService stockInventoryService) {
        this.bookedMovieRepository = bookedMovieRepository;
        this.memberRepository = memberRepository;
        this.movieRepository = movieRepository;
        this.theaterInfoRepository = theaterInfoRepository;
        this.stockInventoryService = stockInventoryService;
    }

    /**
     * 영화 예매
     */
    @Transactional
    public BookedMovie bookingMovie(MovieBookDto dto) {

        // 로그인 한 사람 -> user
        String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new EntityNotFoundException("해당 email의 회원은 존재하지 않습니다.")
        );

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(dto.getScreeningDate(), dateTimeFormatter);

        Movie movie = movieRepository.findByTitleAndScreeningDate(dto.getTitle(), dateTime).orElseThrow(
                () -> new EntityNotFoundException("해당 날짜의 영화가 존재하지 않습니다.")
        );

        // 예매
        BookedMovie bookedMovie = dto.toEntity(member, movie);
        if (bookedMovie.getMovie().getOption().equals(Option.Event)) { // 이벤트 영화이면
            // redis로 동시성 이슈 해결
//            int remainSeat = stockInventoryService.decreaseStock(movie.getId(), dto.getCnt()).intValue();
//            if (remainSeat < 0) {
//                throw new IllegalArgumentException("남은 좌석이 없습니다.");
//            }
            log.info("1");
            boolean success = stockInventoryService.reserveSeat(movie.getTheaterInfo().getId(), dto.getSeatId());
            log.info("2 success : " + success);
            if (!success) {
                throw new IllegalArgumentException(dto.getSeatId() +"좌석은 선택할 수 없습니다.");
            }
            log.info("3");
        } else { // 이벤트 용 영화가 아니면
            // 예매 성공하면 -> 좌석 수 인원수만큼 차감
            TheaterInfo info = theaterInfoRepository.findById(movie.getId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 id의 상영관이 존재하지 않습니다.")
            );
            log.info("{}번 상영관 좌석 차감 전: {}", movie.getId(), info.getRemainSeat());
            info.updateRemainSeat(1); // 한자리씩 예약 가능
            log.info("{}번 상영관 좌석 차감 후: {}", movie.getId(), info.getRemainSeat());
        }

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
    public Page<MovieListDto> movieList(Pageable pageable) {

        Page<BookedMovie> list = bookedMovieRepository.findAll(pageable);

        AtomicInteger start = new AtomicInteger((int) pageable.getOffset());

        Page<MovieListDto> listDtos = list.map(a -> a.listAllFromEntity(start.incrementAndGet()));

        return listDtos;
    }

}
