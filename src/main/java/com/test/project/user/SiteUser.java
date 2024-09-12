package com.test.project.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username; // 사용자 아이디

    private String password; // 사용자 비밀번호

    @Column(unique = true)
    private String email; // 이메일 주소

    private String imageUrl; // 프로필 이미지 경로

    private String phoneNumber; // 휴대폰 번호

    private String snsAgree; // SNS 수신 동의 (yes/no)

    private String name; // 사용자 이름

    // 주소 필드들 (우편번호, 기본 주소, 상세 주소로 분리)
    private String postcode;
    private String basicAddress;
    private String detailAddress;
}
