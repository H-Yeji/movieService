package com.movie.common.service;

import com.movie.member.domain.Role;
import com.movie.member.dto.MemberCreateDto;
import com.movie.member.repository.MemberRepository;
import com.movie.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements CommandLineRunner {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.findByEmail("admin@test.com").isEmpty()) { // 해당 이메일이 없으면 -> admin 추가
            memberService.createMember(MemberCreateDto.builder()
                    .name("admin")
                    .email("admin@test.com")
                    .password("12341234")
                    .role(Role.ADMIN)
                    .build()
            );
        }
    }
}
