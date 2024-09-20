package com.test.project.user;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateForm {

    @NotEmpty(message = "성명을 입력하세요")
    private String name;

    @NotEmpty(message = "이메일을 입력하세요")
    @Email(message = "유효한 이메일 주소를 입력하세요")
    private String email;

    @NotEmpty(message = "연락처를 입력하세요")
    private String phoneNumber;

    private MultipartFile profileImage;  // 이미지 업로드를 위한 필드

    @NotEmpty(message = "우편번호를 입력하세요")  
    private String postcode;

    @NotEmpty(message = "기본 주소를 입력하세요")  
    private String basicAddress;

    @NotEmpty(message = "상세 주소를 입력하세요") 
    private String detailAddress;

    private String newPassword;  

    private String newConfirmPassword;  // 비밀번호 재설정 확인 필드

//    @NotEmpty(message = "주소를 입력하세요")
//    private String address;  
}
