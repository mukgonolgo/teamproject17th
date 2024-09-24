package com.test.project.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$", 
             message = "특수문자, 영어, 숫자 조합으로 6글자 이상 입력해 주세요.")
    private String password;

    @NotEmpty(message = "비밀번호 확인을 입력하세요")
    private String confirmPassword;

    @NotEmpty(message = "이메일을 입력하세요")
    @Email(message = "유효한 이메일 주소를 입력하세요")
    private String emailDomain;

    @NotEmpty(message = "연락처를 입력하세요")
    private String contact;

    @NotEmpty(message = "우편번호를 입력하세요")
    private String postcode;

    @NotEmpty(message = "기본 주소를 입력하세요")
    private String basicAddress;

    @NotEmpty(message = "상세 주소를 입력하세요")
    private String detailAddress;

    private String profileImage;
    private String snsAgree;

    @NotEmpty(message = "닉네임을 입력하세요")
    private String nickname;
    @NotEmpty(message = "회원 유형을 선택하세요")
    private String userType; // 일반회원 or 사업자

}
