package com.test.project.chat;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketChatHandler extends TextWebSocketHandler {

    @Autowired
    private ChatRoomService chatRoomService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // WebSocket 세션 연결 시 쿼리 파라미터 추출
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery();
            Map<String, String> params = parseQueryParams(query);

            String roomId = params.get("roomId");
            String userIdStr = params.get("userId");

            if (roomId != null && userIdStr != null) {
                Long userId = Long.valueOf(userIdStr);
                chatRoomService.joinChatRoom(roomId, userId);
            }
        }

        super.afterConnectionEstablished(session);
    }

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

    private Map<String, String> parseQueryParams(String query) {
        // 쿼리 문자열 파싱
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }
}
