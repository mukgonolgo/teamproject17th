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
    private String emailDomain;

    @NotEmpty(message = "연락처를 입력하세요")
    private String contact;

    // 주소 필드들 (우편번호, 기본 주소, 상세 주소로 분리)
    @NotEmpty(message = "우편번호를 입력하세요")
    private String postcode;

    @NotEmpty(message = "기본 주소를 입력하세요")
    private String basicAddress;

    @NotEmpty(message = "상세 주소를 입력하세요")
    private String detailAddress;

    private String profileImage;
    private String snsAgree;
}
