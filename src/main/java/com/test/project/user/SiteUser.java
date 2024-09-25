package com.test.project.user;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test.project.chat.ChatRoom; // ChatRoom 클래스를 import

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "site_user")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
    @ManyToMany(mappedBy = "members")
    private Set<ChatRoom> chatRooms; // 사용자가 참여하는 채팅방들
    
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom; 

    
}
