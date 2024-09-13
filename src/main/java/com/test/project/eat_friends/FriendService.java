package com.test.project.eat_friends;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
public class FriendService {

    @Autowired
    private FriendRepository friendRepository;

    // 엔티티를 DTO로 변환하는 메서드
    private FriendDTO convertToDTO(Friend friend) {
        FriendDTO dto = new FriendDTO();
        dto.setId(friend.getFriendId());
        dto.setTitle(friend.getFriendTitle());
        dto.setMaxMembers(friend.getFriendMaxMembers());
        dto.setTotalMembers(friend.getFriendTotalMembers());        
        dto.setEventDate(friend.getFriendEventDate());
        dto.setEventTime(friend.getFriendEventTime());
        dto.setMemberName(friend.getFriendMemberName());
        dto.setRoomID(friend.getRoomID()); // roomID를 DTO에 추가
        return dto;
    }

    // DTO를 엔티티로 변환하는 메서드
    private Friend convertToEntity(FriendDTO dto) {
        Friend friend = new Friend();
        friend.setFriendId(dto.getId());
        friend.setFriendTitle(dto.getTitle());
        friend.setFriendMaxMembers(dto.getMaxMembers());
        friend.setFriendTotalMembers(dto.getTotalMembers());
        friend.setFriendEventDate(dto.getEventDate());
        friend.setFriendEventTime(dto.getEventTime());
        friend.setFriendMemberName(dto.getMemberName());
        friend.setRoomID(dto.getRoomID());
        return friend;
    }

    // DTO 목록을 반환하는 메서드
    public List<FriendDTO> getAllFriends() {
        List<Friend> friends = friendRepository.findAll();
        List<FriendDTO> friendDTOs = new ArrayList<>();
        for (Friend friend : friends) {
            friendDTOs.add(convertToDTO(friend));
        }
        return friendDTOs;
    }

    // 단일 DTO를 반환하는 메서드 (예: ID로 검색)
    public FriendDTO getFriendById(Long id) {
        Friend friend = friendRepository.findById(id).orElse(null);
        if (friend != null) {
            return convertToDTO(friend);
        }
        return null;
    }
    
    // 모든 Friend 엔티티를 조회하는 메서드
    public List<Friend> getAllFriendsEntities() {
        return friendRepository.findAll(); // 인스턴스를 통해 호출
    }
    
    public Page<Friend> getFriends(Pageable pageable) {
        return friendRepository.findAll(pageable); // 인스턴스를 통해 호출
    }
    
    public void saveFriend(Friend friend) {
        friendRepository.save(friend);
    }
    
    public FriendService(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }
    
    public String getRoomIdByTitle(String title) {
        Friend friend = friendRepository.findByFriendTitle(title); // 제목으로 Friend 엔티티를 조회
        return friend != null ? friend.getRoomID() : null;
    }
    
    // DTO를 저장하는 메서드
    public void saveFriendDTO(FriendDTO dto) {
        Friend friend = convertToEntity(dto);
        saveFriend(friend);
    }
}
