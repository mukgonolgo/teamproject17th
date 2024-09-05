package com.test.project.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private static final String UPLOAD_DIR = "src/main/resources/static/img/user/";
	
	public SiteUser create(String username, String email, String password, String address, String phoneNumber, MultipartFile imageFile) throws IOException {
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
	
	// 로그인한 사용자명을 알 수 있는 메소드
	public SiteUser getUser(String username) {
		Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
		
		if (siteUser.isPresent()) {
			return siteUser.get();
		} else {
			throw new DataNotFoundException("해당 회원이 없습니다.");
		}
	}
}
