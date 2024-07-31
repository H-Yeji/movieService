package com.movie.member.dto;

import com.movie.member.domain.Member;
import com.movie.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCreateDto {

    private String name;
    private String email;
    private String password;
    private Role role = Role.USER;

    public Member toEntity(String encodedPassword) {

        Member member = Member.builder()
                .name(this.name)
                .email(this.email)
                .password(encodedPassword)
                .role(this.role)
                .build();
        return member;
    }

}
