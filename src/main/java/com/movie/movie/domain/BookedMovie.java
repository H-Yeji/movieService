package com.movie.movie.domain;

import com.movie.member.domain.Member;
import com.movie.movie.dto.BookedMovieListDto;
import com.movie.movie.dto.MyMovieListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookedMovie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "running_movie_id")
    private RunningMovie runningMovie;

    public MyMovieListDto listFromEntity(int no) {
        MyMovieListDto myMovieListDto = MyMovieListDto.builder()
                .no(no)
                .title(this.getRunningMovie().getMovie().getTitle())
                .date(String.valueOf(this.getRunningMovie().getScreeningDate()))
                .build();
        return myMovieListDto;
    }

    public BookedMovieListDto listAllFromEntity(int no) {
        BookedMovieListDto myBookedMovieListDto = BookedMovieListDto.builder()
                .no(no)
                .memberEmail(this.member.getEmail())
                .runningMovie(this.runningMovie)
                .build();
        return myBookedMovieListDto;
    }
}
