package com.test.project.chat;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.test.project.user.SiteUser;
import com.test.project.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository roomRepository;
    private final ChatRepository chatRepository;
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> roomUsers = new ConcurrentHashMap<>();

    // 모든 채팅방을 조회
    public List<ChatRoom> findAllRoom() {
        return roomRepository.findAll();
    }

    // ID로 채팅방을 조회
    public ChatRoom findRoomById(String roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
    }

    // 채팅방 생성 또는 기존 채팅방 조회
    public ChatRoom createChatRoom(String roomId, String name, int maxMembers) {
        return roomRepository.findById(roomId)
            .orElseGet(() -> roomRepository.save(new ChatRoom(
                roomId,
                name,
                maxMembers,
                0, // 기본적으로 현재 인원 수는 0으로 설정
                new HashSet<>() // 빈 Set으로 초기화
            )));
    }

    // 채팅 메시지 저장
    public Chat createChatMessage(String roomId, SiteUser sender, String message) {
        ChatRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        return chatRepository.save(Chat.createChat(room, sender, message));
    }

    // 특정 채팅방의 모든 채팅 메시지를 조회
    public List<Chat> getChatList(String roomId) {
        return chatRepository.findAllByRoom_RoomId(roomId);
    }

    // 모든 채팅 메시지 가져오기
    public List<Chat> getAllChats() {
        return chatRepository.findAll(); // 모든 채팅 메시지 가져오는 로직
    }

    @Transactional
    public ChatRoom updateChatRoom(ChatRoom chatRoom) {
        // 채팅방 정보를 업데이트
        return roomRepository.save(chatRoom);
    }

    public ChatRoom getChatRoom(String roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found with roomId: " + roomId));
    }

    // 채팅방의 모든 사용자 조회
    public Set<SiteUser> getMembersInChatRoom(String roomId) {
        ChatRoom chatRoom = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("ChatRoom not found with id: " + roomId));
        return chatRoom.getMembers();
    }

    @Transactional
    public boolean addUserToRoom(String roomId, SiteUser user) {
        ChatRoom chatRoom = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        if (chatRoom.getMembers().contains(user)) {
            return false; // 이미 방에 있는 경우
        }
        if (chatRoom.getChatcurrentMembers() < chatRoom.getChatmaxMembers()) {
            chatRoom.getMembers().add(user);
            chatRoom.setChatcurrentMembers(chatRoom.getChatcurrentMembers() + 1);
            user.getChatRooms().add(chatRoom);
            roomRepository.save(chatRoom);
            return true;
        }
        return false; // 방이 가득 찬 경우
    }

    @Transactional
    public boolean removeUserFromRoom(String roomId, SiteUser user) {
        ChatRoom chatRoom = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        if (chatRoom.getMembers().remove(user)) {
            chatRoom.setChatcurrentMembers(chatRoom.getChatcurrentMembers() - 1);
            user.getChatRooms().remove(chatRoom);
            roomRepository.save(chatRoom);
            return true;
        }
        return false;
    }

    // 사용자가 방을 떠나는 로직
    public boolean leaveRoom(String roomId, String username) {
        Set<String> usersInRoom = roomUsers.get(roomId);
        if (usersInRoom != null && usersInRoom.remove(username)) {
            // 웹소켓 세션에서 사용자 제거
            WebSocketSession userSession = userSessions.get(username);
            if (userSession != null && userSession.isOpen()) {
                try {
                    userSession.sendMessage(new TextMessage("You have been removed from the room: " + roomId));
                    userSession.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    // 웹소켓 세션을 저장하는 메소드
    public void registerSession(String username, WebSocketSession session) {
        userSessions.put(username, session);
    }

    // 웹소켓 세션을 제거하는 메소드
    public void unregisterSession(String username) {
        userSessions.remove(username);
    }
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addUserToChatRoom(String chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
        SiteUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 채팅방에 사용자를 추가
        chatRoom.getMembers().add(user);
        // 사용자의 채팅방 목록에 현재 채팅방 추가
        user.getChatRooms().add(chatRoom);

        // 변경된 엔티티를 저장
        chatRoomRepository.save(chatRoom);
        userRepository.save(user);
    }
    
    public ChatRoom getChatRoomWithMembers(String roomId) {
        return roomRepository.findById(roomId)
                .map(chatRoom -> {
                    // Explicitly load members if necessary
                    chatRoom.getMembers().size(); // Initialize the collection
                    return chatRoom;
                })
                .orElseThrow(() -> new RuntimeException("Chat room not found with roomId: " + roomId));
    }
    


    // 팩토리 메서드
    private ChatRoomDTO createChatRoomDTO(ChatRoom chatRoom) {
        return ChatRoomDTO.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getName())
                .currentMembers(chatRoom.getChatcurrentMembers())
                .maxMembers(chatRoom.getChatmaxMembers())
                .build();
    }
    public ChatRoomStatusDTO getChatRoomStatus() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        List<ChatRoomDTO> chatRoomDTOs = chatRooms.stream()
                .map(this::createChatRoomDTO)
                .collect(Collectors.toList());
        return ChatRoomStatusDTO.builder()
                .chatRooms(chatRoomDTOs)
                .build();
    }
}
