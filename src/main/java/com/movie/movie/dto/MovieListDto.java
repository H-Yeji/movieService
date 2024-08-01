package com.movie.movie.dto;

import com.movie.member.domain.Member;
import com.movie.movie.domain.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieListDto {

    private int no;
    private String memberEmail;
    private Movie movie;
}
