package com.test.project.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/signup")
public class EmailController {

    @Autowired
    private EmailSendService emailSendService;

    // 이메일 전송
    @PostMapping("/email")
    public ResponseEntity<Map<String, String>> mailSend(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        String code = emailSendService.joinEmail(emailRequestDto.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        return ResponseEntity.ok(response);
    }

    // 이메일 인증 확인
    @PostMapping("/emailAuth")
    public ResponseEntity<Map<String, String>> authCheck(@RequestBody @Valid EmailCheckDto emailCheckDto) {
        Boolean isValid = emailSendService.checkAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());
        
        Map<String, String> response = new HashMap<>();
        if (isValid) {
            response.put("message", "이메일 인증 성공!");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "이메일 인증 실패! 잘못된 인증번호이거나 만료된 번호입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
