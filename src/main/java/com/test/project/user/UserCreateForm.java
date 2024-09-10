package com.test.project.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {

    @NotEmpty(message = "성명을 입력하세요")
    private String name;

    @NotEmpty(message = "아이디를 입력하세요")
    private String username;

    @NotEmpty(message = "비밀번호를 입력하세요")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    private String password;

    @NotEmpty(message = "비밀번호 확인을 입력하세요")
    private String confirmPassword;

    @NotEmpty(message = "이메일을 입력하세요")
    @Email(message = "유효한 이메일 주소를 입력하세요")
    private String emailDomain;  // 이메일 도메인 필드

    @NotEmpty(message = "연락처를 입력하세요")
    private String contact;

    @NotEmpty(message = "주소를 입력하세요")
    private String address;

    private String profileImage;  // 프로필 이미지 필드

    private String snsAgree;  // SNS 수신 동의
}
