package com.movie.member.controller;

import com.movie.common.auth.JwtTokenProvider;
import com.movie.common.dto.CommonErrorDto;
import com.movie.common.dto.CommonResDto;
import com.movie.common.dto.EmailRequestDto;
import com.movie.common.service.MailService;
import com.movie.member.domain.Member;
import com.movie.member.dto.MemberCreateDto;
import com.movie.member.dto.MemberLoginDto;
import com.movie.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailService mailService;

    @Autowired
    public MemberController(MemberService memberService,
                            JwtTokenProvider jwtTokenProvider,
                            MailService mailService) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailService = mailService;
    }

    /**
     * 이메일 인증번호로 회원가입
     */
    @PostMapping("/create")
    public ResponseEntity<?> createMember(@RequestBody MemberCreateDto dto) {

        try {
            mailService.authEmail(dto.getEmail(), dto);
            return new ResponseEntity<>("이메일로 인증 코드를 발송했습니다.", HttpStatus.OK);
        } catch (MailSendException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), "이메일 전송을 실패했습니다.");
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 회원 가입
     */
    @PostMapping("/create/verified")
    public ResponseEntity<?> verifyAuthCode(@RequestBody EmailRequestDto requestDto) {

        boolean isValid = mailService.verifyAuthCode(requestDto.getEmail(), requestDto.getCode());

        if (isValid) {
            try {
                // isValid가 true(= 인증코드 일치함)이면, redis에 저장해둔 dto값 꺼내오기
                MemberCreateDto dto = mailService.getUserData(requestDto.getEmail(), MemberCreateDto.class);
                log.info("redis에서 찾아온 membercreatedto: {}", dto);

                Member member = memberService.createMember(dto);
                CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, member.getEmail()+"님 환영합니다.",  member.getId());
                return new ResponseEntity<>(commonResDto, HttpStatus.OK);
            } catch (IllegalArgumentException e) {
                CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
                return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("인증 코드가 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
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
