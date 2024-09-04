package com.test.project.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    
    @Size(min=3, max=30)
    @NotEmpty(message="사용자 아이디를 꼭 입력해주세요.")
    private String username;
    
    @NotEmpty(message="사용자 비밀번호를 꼭 입력해주세요.")
    private String password1;
    
    @NotEmpty(message="사용자 비밀번호를 확인해 주셔야 합니다.")
    private String password2;
    
    @NotEmpty(message="이메일은 필수사항입니다.")
    @Email
    private String email;
    
    @NotEmpty(message="주소는 필수사항입니다.")
    private String address; // 주소 추가
    
    @NotEmpty(message="휴대폰 번호는 필수사항입니다.")
    private String phoneNumber; // 휴대폰 번호 추가
}
