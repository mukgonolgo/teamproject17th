package com.test.project.chat; // 패키지 경로를 적절히 수정하세요

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomJoinRequest {
    private String chatRoomId;
    private String username;
    



    // 모든 필드를 사용하는 생성자
    public ChatRoomJoinRequest(String chatRoomId, String username) {
        this.chatRoomId = chatRoomId;
        this.username = username;
        
    }

 
}
