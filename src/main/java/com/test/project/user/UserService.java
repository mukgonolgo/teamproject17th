package com.test.project.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.DataNotFoundException;
import com.test.project.chat.ChatRoom;
import com.test.project.chat.ChatRoomRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final SiteUserRepository siteUserRepository;
    private static final String UPLOAD_DIR = "src/main/resources/static/img/user/";

    // 회원가입 로직
    public SiteUser create(String username, String emailDomain, String password, String postcode, String basicAddress,
                           String detailAddress, String phoneNumber, String snsAgree, MultipartFile imageFile, String name)
            throws IOException {

        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(emailDomain);
        user.setPostcode(postcode);
        user.setBasicAddress(basicAddress);
        user.setDetailAddress(detailAddress);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(password));
        user.setSnsAgree(snsAgree);
        user.setName(name);

        // 프로필 이미지 저장 로직
        if (!imageFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, imageFile.getBytes());
            user.setImageUrl("/img/user/" + fileName);
        }

        this.userRepository.save(user);
        return user;
    }

    // 이메일 전송 로직
    public void sendEmail(String toEmail, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByUsername(username);

        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("해당 회원이 없습니다.");
        }
    }

    // 아이디 중복 확인 로직
    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // 아이디 찾기 로직 (email 사용)
    public String findIdByNameAndEmail(String name, String email) {
        Optional<SiteUser> user = userRepository.findByNameAndEmail(name, email);
        return user.map(SiteUser::getUsername).orElse(null);
    }

    // 비밀번호 찾기 로직 (이름과 아이디만 사용)
    public boolean verifyUserForPasswordReset(String name, String username) {
        Optional<SiteUser> user = userRepository.findByNameAndUsername(name, username);
        return user.isPresent();
    }

    public void resetPassword(String username, String newPassword) {
        System.out.println("비밀번호 재설정 요청된 아이디: " + username);
        Optional<SiteUser> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            SiteUser siteUser = user.get();
            siteUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(siteUser);
        } else {
            System.out.println("사용자 정보를 찾을 수 없습니다: " + username);
            throw new DataNotFoundException("해당 회원이 없습니다.");
        }
    }

    public String checkUserDetails(String name, String username) {
        Optional<SiteUser> userOptional = userRepository.findByNameAndUsername(name, username);

        if (!userOptional.isPresent()) {
            return "사용자 정보가 일치하지 않습니다.";
        }

        return "일치하는 사용자를 찾았습니다.";
    }

    // 사용자 프로필 업데이트 로직 (주소, 비밀번호 포함)
    public void updateUserProfile(String username, UserUpdateForm form) throws IOException {
        // 기존 사용자 정보 찾기
        SiteUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("사용자를 찾을 수 없습니다."));

        // 사용자 정보 업데이트
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPhoneNumber(form.getPhoneNumber());

        // 주소 정보를 각각의 필드에 저장
        user.setPostcode(form.getPostcode());         // 우편번호 저장
        user.setBasicAddress(form.getBasicAddress()); // 기본 주소 저장
        user.setDetailAddress(form.getDetailAddress()); // 상세 주소 저장

        // 프로필 이미지 업데이트 로직
        if (form.getProfileImage() != null && !form.getProfileImage().isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + form.getProfileImage().getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, form.getProfileImage().getBytes());
            user.setImageUrl("/img/user/" + fileName);
        }

        // 비밀번호 변경
        if (form.getNewPassword() != null && form.getNewPassword().equals(form.getNewConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        }

        // 사용자 정보 저장
        userRepository.save(user);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public SiteUser findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public void updateUserChatRoom(Long userId, String roomId) {
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

    public SiteUser getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public boolean removeUserFromChatRoom(String roomId, Long userId) {
        // 채팅방과 사용자 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));
        SiteUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자와 채팅방 관계에서 제거
        boolean removedFromRoom = chatRoom.getMembers().remove(user);
        boolean removedFromUser = user.getChatRooms().remove(chatRoom);

        if (removedFromRoom && removedFromUser) {
            // 채팅방 및 사용자 정보 업데이트
            chatRoom.setChatcurrentMembers(chatRoom.getChatcurrentMembers() - 1);
            chatRoomRepository.save(chatRoom);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public String getUserName(Long id) {
        SiteUser user = siteUserRepository.findById(id).orElse(null);
        return user != null ? user.getName() : null;
    }
}
