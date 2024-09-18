package com.test.project.chat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.project.user.SiteUser;
import com.test.project.user.SiteUserRepository;

import jakarta.transaction.Transactional;

@Service
public class ChatRoomService {

    @Autowired
    private SiteUserRepository siteUserRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public void joinChatRoom(String roomId, Long userId) {
        // 채팅방과 사용자 가져오기
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid roomId"));
        SiteUser siteUser = siteUserRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        // 채팅방에 사용자 추가
        chatRoom.getMembers().add(siteUser);
        chatRoom.setChatcurrentMembers(chatRoom.getChatcurrentMembers() + 1);

        // 사용자 채팅방 업데이트
        siteUser.setChatRoom(chatRoom);
        siteUserRepository.save(siteUser);

        // 채팅방 업데이트
        chatRoomRepository.save(chatRoom);
    }

    
    public List<ChatRoomDTO> getAllChatRooms() {
        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        return chatRooms.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ChatRoomDTO convertToDTO(ChatRoom chatRoom) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setRoomId(chatRoom.getRoomId());
        dto.setCurrentMembers(chatRoom.getChatcurrentMembers());
        dto.setMaxMembers(chatRoom.getChatmaxMembers());
        dto.setMemberNames(chatRoom.getMembers().stream()
                          .map(member -> new MemberDTO(member.getUsername(), member.getId()))
                          .collect(Collectors.toList()));
        return dto;
    }
    public ChatRoom getChatRoomById(String roomId) {
        return chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));
    }
    
    @Transactional
    public void createChatRoomWithMembers(String chatRoomId, List<String> usernames) {
        // Find or create the ChatRoom
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        // Check if users exist by username and collect them
        Set<SiteUser> validUsers = usernames.stream()
            .map(username -> siteUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username)))
            .collect(Collectors.toSet());

        // Add valid users to the chatRoom's members
        chatRoom.getMembers().addAll(validUsers);

        // Save the updated chatRoom
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatRoom findById(String id) {
        return chatRoomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("ChatRoom not found"));
    }

}
 
