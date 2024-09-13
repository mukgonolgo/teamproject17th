package com.test.project.chat;

import java.io.IOException;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketChatHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 클라이언트로부터 받은 메시지를 처리합니다.
        String payload = message.getPayload();
        // 예시로 받은 메시지를 그대로 클라이언트로 전송
        try {
            session.sendMessage(new TextMessage("Received: " + payload));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
