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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private static final String UPLOAD_DIR = "src/main/resources/static/img/user/";

    // 회원가입 로직
    public SiteUser create(String username, String emailDomain, String password, String address, String phoneNumber, String snsAgree, MultipartFile imageFile, String name) throws IOException {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(emailDomain);
        user.setAddress(address);
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

    
}
