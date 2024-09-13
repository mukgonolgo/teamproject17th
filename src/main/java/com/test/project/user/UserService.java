package com.test.project.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.chat.ChatRoom;
import com.test.project.chat.ChatRoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
    private final ChatRoomRepository chatRoomRepository; 
	private static final String UPLOAD_DIR = "src/main/resources/static/img/user/";

	public SiteUser create(String username, String email, String password, String address, String phoneNumber,
			MultipartFile imageFile) throws IOException {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setEmail(email);
		user.setAddress(address); // 주소 추가
		user.setPhoneNumber(phoneNumber); // 휴대폰 번호 추가
		user.setPassword(passwordEncoder.encode(password)); // 암호화된 비밀번호 설정

		// 회원 프로필 이미지 등록한다면 해당 이미지 이름도 DB 저장
		if (!imageFile.isEmpty()) {
			// 고유한 이미지 이름 생성
			String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
			// 파일 저장 경로
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			// 디렉토리가 없으면 생성
			Files.createDirectories(filePath.getParent());
			// 파일 저장
			Files.write(filePath, imageFile.getBytes());
			// 사용자 엔티티에 이미지 경로 설정
			user.setImageUrl("/img/user/" + fileName);
		}

		this.userRepository.save(user);
		return user;
	}


	
	  // 사용자 ID로부터 사용자 정보 가져오기
    public SiteUser getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // 사용자 이름으로 사용자 정보 가져오기
    public SiteUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }



	public SiteUser findByUsername(String username) {
		// 
		 return userRepository.findByUsername(username).orElse(null);
	}


	 @Transactional
	    public void updateUserChatRoom(Long userId, String  roomId) {
	        SiteUser user = userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
	        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
	                .orElseThrow(() -> new RuntimeException("ChatRoom not found with ID: " + roomId));

	        // 채팅방을 사용자의 채팅방 목록에 추가
	        user.getChatRooms().add(chatRoom);
	        userRepository.save(user);
	    }
	 
	 @Transactional
	    public void addChatRoomToUser(Long userId, String roomId) {
	        SiteUser user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
	            .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

	        user.getChatRooms().add(chatRoom);
	        userRepository.save(user);
	    }

	    @Transactional
	    public void removeChatRoomFromUser(Long userId, String roomId) {
	        SiteUser user = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
	            .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

	        user.getChatRooms().remove(chatRoom);
	        userRepository.save(user);
	    }



}
