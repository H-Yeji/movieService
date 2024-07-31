package com.movie.member.service;

import com.movie.member.domain.Member;
import com.movie.member.dto.MemberCreateDto;
import com.movie.member.dto.MemberLoginDto;
import com.movie.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    String memberEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//    Member member = memberRepository.findByEmail(memberEmail).orElseThrow(
//            () -> new EntityNotFoundException("해당 email의 회원이 없습니다.")
//    );
    /**
     * 회원 가입
     */
    @Transactional
    public Member createMember(MemberCreateDto dto) {
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        if (dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호의 길이가 짧습니다.");
        }
        Member member = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        Member savedMember = memberRepository.save(member);
        return savedMember;
    }

    /**
     * 로그인
     */
    public Member login(MemberLoginDto dto) {

        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 이메일의 회원은 없습니다.")
        );

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

}
