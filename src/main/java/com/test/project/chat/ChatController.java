package com.test.project.chat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.project.user.SiteUser;
import com.test.project.user.UserRepository;
import com.test.project.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final UserService userService;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
 

    // 채팅 페이지 렌더링
    @GetMapping("/chat")
    public String getChatPage(Model model, @RequestParam(required = false) String id, Authentication authentication, HttpSession session) {
        // 사용자 정보 가져오기
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        
        SiteUser user = userService.getUser(username);
        
        if (user != null) {
            Long userId = user.getId(); // 유저 ID 가져오기
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userID", userId);
            logger.info("User found. Username: {}, User ID: {}", user.getUsername(), user.getId());
        } else {
            logger.warn("User not found. Username: {}", username);
        }

        // 세션에서 userId 가져오기
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId != null) {
            model.addAttribute("queryUserId", id);
        }

        // 채팅 리스트 가져오기
        model.addAttribute("chatList", chatService.getChatList(id)); // 특정 채팅방의 채팅 리스트
        
        // 채팅 페이지 템플릿 반환
        return "chat"; // chat.html 템플릿을 반환 (혹은 필요한 경우 "eat_friends"로 변경 가능)
    }
    // 모든 채팅 가져오기 (API)
    @GetMapping("/chat/all")
    @ResponseBody
    public List<Chat> getChatPage() {
        List<Chat> chatList = chatService.getAllChats(); // 모든 채팅 가져오기
        return chatList; // JSON 형식으로 반환
    }
    

 

    // 채팅 메시지 전송
    @MessageMapping("/chat/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessageDTO chat(@DestinationVariable("roomId") String roomId, ChatMessageDTO message, Authentication authentication) {
        try {
            String username = authentication.getName();

            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("Username must not be null or empty");
            }

            SiteUser sender = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

            // 채팅방이 존재하는지 확인
            ChatRoom chatRoom = chatService.getChatRoom(roomId);
            if (chatRoom == null) {
                // 채팅방이 존재하지 않는 경우에만 생성
                chatRoom = chatService.createChatRoom(roomId, "Default Room Name", 30);
            }

            // 현재 인원 수를 1 증가시킴
            chatRoom.setChatcurrentMembers(chatRoom.getChatcurrentMembers() + 1);
            chatService.updateChatRoom(chatRoom); // 채팅방 정보를 업데이트

            Chat chat = chatService.createChatMessage(roomId, sender, message.getMessage());

            return ChatMessageDTO.builder()
                    .roomId(roomId)
                    .username(sender.getUsername())
                    .senderEmail(sender.getEmail())
                    .message(chat.getMessage())
                    .sendDate(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error while sending message", e);
        }
    }

    // 새 채팅방 생성
    @PostMapping("/create-room")
    public ResponseEntity<Map<String, Object>> createChatRoom(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        String name = request.get("name");
        int maxMembers = 30; // 최대 인원 수

        try {
            ChatRoom chatRoom = chatService.createChatRoom(roomId, name, maxMembers);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("roomId", chatRoom.getRoomId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 채팅방 페이지 렌더링
    @GetMapping("/chat/room/{roomId}")
    public String joinRoom(@PathVariable("roomId") String roomId, Model model, Authentication authentication, HttpSession session) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        SiteUser user = userService.getUser(username);

        logger.info("Received request to join chat room. User: {}", username);

        if (user != null) {
            model.addAttribute("username", userDetails.getUsername());
        } else {
            logger.warn("User not found. Username: {}", username);
        }
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userID", user.getId());

        // 채팅방의 채팅 데이터 가져오기
        List<Chat> chatList = chatService.getChatList(roomId); // 특정 채팅방의 채팅 리스트
        model.addAttribute("roomId", roomId);
        model.addAttribute("chatList", chatList);
        return "room"; // Thymeleaf 템플릿 파일명
    }

    // 채팅방의 데이터 제공 (채팅 목록 포함)
    @GetMapping("/chat/api/{roomId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChatRoomData(@PathVariable("roomId") String roomId) {
        try {
            List<Chat> chatList = chatService.getChatList(roomId);
            Map<String, Object> response = new HashMap<>();
            response.put("chatList", chatList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching chat room data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An error occurred while fetching chat room data"));
        }
    }

    @GetMapping("/room/{roomId}/info")
    public ResponseEntity<Map<String, Object>> getChatRoomInfo(@PathVariable("roomId") String roomId) {
        try {
            ChatRoom chatRoom = chatService.getChatRoom(roomId);
            if (chatRoom == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Chat room not found"));
            }

            Set<SiteUser> members = chatService.getMembersInChatRoom(roomId);
            List<Map<String, Object>> membersList = members.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("username", user.getUsername());
                    return userMap;
                })
                .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("roomName", chatRoom.getName());
            response.put("currentMembers", chatRoom.getChatcurrentMembers());
            response.put("maxMembers", chatRoom.getChatmaxMembers());
            response.put("members", membersList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching chat room info for roomId: {}", roomId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "An error occurred while fetching chat room info"));
        }
    }

    /*
     * 채팅창 있는지 확인
     * */
    @GetMapping("/room/{roomId}/exists")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkRoomExists(@PathVariable("roomId") String roomId) {
        boolean exists = chatService.getChatRoom(roomId) != null;
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/room/{roomId}/leave")
    public ResponseEntity<Map<String, Object>> leaveChatRoom(@PathVariable("roomId") String roomId, @RequestBody Map<String, String> payload) {
        String username = payload.get("username");

        // 사용자와 채팅방의 관계를 관리하는 로직
        boolean success = chatService.leaveRoom(roomId, username);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (success) {
            response.put("message", "User left the chat room successfully.");
        } else {
            response.put("message", "Failed to leave the chat room.");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/chatRoom/{roomId}")
    public String getChatRoom(@PathVariable("roomId") String roomId, Model model) {
        ChatRoom chatRoom = chatService.getChatRoomWithMembers(roomId);
        model.addAttribute("chatRoom", chatRoom);
        return "eat_friends";
    }
    @GetMapping("/chat/chat-room-status/{roomId}")
    public ResponseEntity<ChatRoomDTO> getChatRoomStatus(@PathVariable("roomId") String roomId) {
        ChatRoom chatRoom = chatService.getChatRoom(roomId);
        if (chatRoom == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(
                chatRoom.getRoomId(),
                chatRoom.getName(),
                chatRoom.getChatcurrentMembers(),
                chatRoom.getChatmaxMembers()
        );

        return ResponseEntity.ok(chatRoomDTO);
    }
}
