package com.test.project.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Builder
public class ChatMessageDTO {

    private String roomId;
    private String username;
    
    private Long senderId;
    private String senderEmail;
    private String message;
    private LocalDateTime sendDate;

}