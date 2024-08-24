package com.movie.movie.domain;

import com.movie.movie.dto.MovieListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunningMovie { // 상영중인 영화

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    @JoinColumn(name = "theater_id")
    private TheaterInfo theaterInfo;

    @Column(nullable = false)
    private Long remainSeat;

    @Column(nullable = false)
    private LocalDateTime screeningDate; // 상영날짜 및 시간

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Option option = Option.General;

    @Column(nullable = false)
    private Integer price;


    public MovieListDto listFromEntity() {
        MovieListDto movie = MovieListDto.builder()
                .id(this.id)
                .title(this.getMovie().getTitle())
                .runningDateTime(String.valueOf(this.screeningDate))
                .remainSeat(this.remainSeat)
                .build();

        return movie;
    }

    // 예약된 후 남은 자리 차감
    public void updateRemainSeat(int cnt) {
        this.remainSeat = this.remainSeat - cnt;
    }
}
