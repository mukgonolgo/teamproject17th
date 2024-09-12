package com.test.project.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.data.redis.core.ValueOperations;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailSendService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSendService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisConfig redisConfig;

    private int authNumber;

    @Value("${spring.mail.username}")
    private String serviceName;

    public void makeRandomNum() {
        Random r = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber);
    }

    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom);
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        // 인증번호 Redis에 저장
        valOperations.set(toMail, Integer.toString(authNumber), 180, TimeUnit.SECONDS);

        // 저장된 값 확인을 위해 다시 읽어오기
        String savedAuthNumber = valOperations.get(toMail);
        logger.info("Redis에 저장된 인증번호: {}", savedAuthNumber);
    }

    public String joinEmail(String email) {
        makeRandomNum();
        String customerMail = email;
        String title = "회원 가입을 위한 이메일입니다!";
        String content = "이메일 인증번호는 " + authNumber + "입니다.";
        mailSend(serviceName, customerMail, title, content);
        return Integer.toString(authNumber);
    }

    /* 인증번호 확인 */
    public Boolean checkAuthNum(String email, String authNum) {
        ValueOperations<String, String> valOperations = redisConfig.redisTemplate().opsForValue();
        String code = valOperations.get(email);
        return Objects.equals(code, authNum);
    }
}
