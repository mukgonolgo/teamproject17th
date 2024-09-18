package com.test.project.chat;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.test.project.user.UserDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
	
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 리스트 보기
     */
    @GetMapping("/roomList")
    public String roomList(Model model) {
        List<ChatRoom> roomList = chatService.findAllRoom();
        model.addAttribute("roomList", roomList);
        return "eat_friends";
    }

    /**
     * 방만들기 폼
     */
    @GetMapping("/roomForm")
    public String roomForm() {
    	  return "eat_friends";
    }
    @GetMapping("/chatRoom/{roomId}")
    public String getChatRoom(@PathVariable String roomId, Model model) {
        ChatRoom chatRoom = chatService.findRoomById(roomId);
        model.addAttribute("chatRoom", chatRoom);
        return "eat_friends"; // Thymeleaf 템플릿 파일 이름
    }
    
  
    
}