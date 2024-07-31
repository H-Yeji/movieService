package com.movie.member.controller;

import com.movie.common.auth.JwtTokenProvider;
import com.movie.common.dto.CommonErrorDto;
import com.movie.common.dto.CommonResDto;
import com.movie.member.domain.Member;
import com.movie.member.dto.MemberCreateDto;
import com.movie.member.dto.MemberLoginDto;
import com.movie.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 회원 가입
     */
    @PostMapping("/create")
    public ResponseEntity<?> createMember(@RequestBody MemberCreateDto dto) {

         try {
             Member member = memberService.createMember(dto);

             CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, member.getEmail()+"님 환영합니다.",  member.getId());
             return new ResponseEntity<>(commonResDto, HttpStatus.OK);
         } catch (IllegalArgumentException e) {
             CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
             return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
         }
    }

    /**
     * 로그인
     */
    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto dto) {

        try {
            Member loginMember = memberService.login(dto);

            // 토큰 생성
            String token = jwtTokenProvider.createToken(loginMember.getEmail(), loginMember.getRole().toString());

            // 토큰 사용자에게 반환
            Map<String, Object> loginInfo = new HashMap<>();
            loginInfo.put("id", loginMember.getId());
            loginInfo.put("token", token);

            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, loginMember.getEmail()+"님 로그인을 성공하셨습니다.", loginInfo);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);

        } catch (EntityNotFoundException | IllegalArgumentException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }

    }
}
