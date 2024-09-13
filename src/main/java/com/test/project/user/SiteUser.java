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
    private String username;
    
    private String password;
    
    @Column(unique = true)
    private String email;
    
    private String imageUrl; // 이미지 업로드 위치
    
    private String address; // 주소 추가
    
    private String phoneNumber; // 휴대폰 번호 추가

    @ManyToMany(mappedBy = "members")
    private Set<ChatRoom> chatRooms; // 사용자가 참여하는 채팅방들
    
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom; 
    
}
