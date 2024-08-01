package com.movie.movie.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String movieNum; // 영화 고유의 번호

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre; // 장르

    @Column(nullable = false)
    private LocalDateTime screeningDate; // 상영날짜 및 시간

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Option option = Option.General;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String imagePath;

    /**
     * theater info 테이블과 연관관계
     */
    @OneToOne
    @JoinColumn(name = "theater_id", nullable = false)
    private TheaterInfo theaterInfo; // 하나의 영화관에서 하나의 영화만 상영 가능
    // 추후에 상영중 영화가 종료되면 > 영화 내역 삭제하고 info 테이블에서 좌석수 되돌리기

    public void updateImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
