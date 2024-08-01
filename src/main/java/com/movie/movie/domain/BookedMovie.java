package com.movie.movie.domain;

import com.movie.member.domain.Member;
import com.movie.movie.dto.MovieListDto;
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
    @JoinColumn(name = "movie_id")
    private Movie movie;

    public MyMovieListDto listFromEntity(int no) {
        MyMovieListDto myMovieListDto = MyMovieListDto.builder()
                .no(no)
                .title(this.getMovie().getTitle())
                .date(String.valueOf(this.getMovie().getScreeningDate()))
                .build();
        return myMovieListDto;
    }

    public MovieListDto listAllFromEntity(int no) {
        MovieListDto myMovieListDto = MovieListDto.builder()
                .no(no)
                .memberEmail(this.member.getEmail())
                .movie(this.movie)
                .build();
        return myMovieListDto;
    }
}
