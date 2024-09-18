package com.test.project.eat_friends;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.test.project.user.SiteUser;
import com.test.project.user.UserService;
import com.test.project.chat.ChatRoom;

import com.test.project.chat.ChatService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class Friend_controller {
    
    private final UserService userService;
    private final FriendService friendService; // FriendService를 추가
    private final ChatService chatService; // ChatRoomService를 추가

    @GetMapping("/eat_friends/{id}")
    public String eatFriends(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        // 사용자 정보를 추가
        if (userDetails != null) {
            SiteUser user = userService.getUser(userDetails.getUsername());        
            model.addAttribute("profileImage", user.getImageUrl());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userId", user.getId());
            
            // `id`를 사용하여 `FriendDTO` 객체를 조회
            FriendDTO friendDTO = friendService.getFriendById(id);
            if (friendDTO != null) {
                model.addAttribute("friendTitle", friendDTO.getTitle());
                model.addAttribute("roomId", friendDTO.getRoomID());
                
                // `roomId`를 사용하여 `ChatRoom` 객체를 조회
                ChatRoom chatRoom = chatService.findRoomById(friendDTO.getRoomID());
                model.addAttribute("chatRoom", chatRoom);
            }
            
            // 친구 목록을 추가
            List<FriendDTO> friends = friendService.getAllFriends();
            model.addAttribute("eatfriends", friends);
        }

        // Thymeleaf 템플릿 이름 반환
        return "eat_friends";
    }

    @GetMapping("/add_friend")  // URL 매핑 확인
    public String showForm(Model model) {
        model.addAttribute("friend", new FriendDTO()); // 빈 FriendDTO를 모델에 추가
        return "formtest"; // 폼을 표시할 Thymeleaf 템플릿 이름
    }
    
    @PostMapping("/eat_friends/add")
    public String addFriend(@ModelAttribute FriendDTO friendDTO) {
        Friend friend = new Friend();
        friend.setFriendTitle(friendDTO.getTitle());
        friend.setFriendEventDate(friendDTO.getEventDate());
        friend.setFriendEventTime(friendDTO.getEventTime());
        friend.setFriendTotalMembers(friendDTO.getTotalMembers());
        friend.setFriendMaxMembers(friendDTO.getMaxMembers());
        friend.setFriendMemberName(friendDTO.getMemberName());
        friend.setRoomID(friendDTO.getRoomID());
        friendService.saveFriend(friend); // Friend 엔티티를 저장
        return "redirect:/eat_friends"; // 폼 제출 후 목록 페이지로 리디렉션
    }
}
