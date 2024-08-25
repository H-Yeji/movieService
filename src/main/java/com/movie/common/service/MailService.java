package com.movie.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;


    /**
     * 메일 보내기
     */
    public void authEmail(String email, Object dto) {

        // 6자리 랜덤 수 생성
        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        // 메일 보내기
        sendAuthEmail(email, authKey);

        // redis에 5분동안 email, authkey 저장
        redisUtil.setExpire(email, authKey, 60 * 5L);
        // 회원가입 정보도 redis에 5분동안 저장 (이메일 말고 나머지)
        redisUtil.setExpire(email+":data", dto, 60 * 5L);

    }

    private void sendAuthEmail(String email, String authKey) {

        String subject = "[Movie] Authorization";
        String message = "인증번호는 " + authKey + "입니다.";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 인증 코드와 일치여부 판단 => true/false로 반환
     */
    public boolean verifyAuthCode(String email, String code) {
        String storedCode = redisUtil.getData(email, String.class);
        return code.equals(storedCode);
    }

    /**
     * 인증코드와 일치할 경우 -> redis에 저장해둔 회원가입 dto값 불러오기
     */
     public <T> T getUserData(String email, Class<T> clazz) {
         return redisUtil.getData(email + ":data", clazz);
     }
}
