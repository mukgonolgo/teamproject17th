package com.test.project.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/ws/chat/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatRoomDTO sendMessage(@DestinationVariable String roomId, ChatRoomDTO message) {
        return message;
    }}
