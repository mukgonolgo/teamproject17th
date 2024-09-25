package com.test.project.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public SiteUser create(String username, String emailDomain, String password, String postcode, String basicAddress,
			String detailAddress, String phoneNumber, String snsAgree, MultipartFile imageFile, String name,
			String nickname,String userType) throws IOException {

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
		user.setNickname(nickname); // 닉네임 저장
		user.setUserType(userType); // userType 저장
		
		 // userType에 따른 approvalStatus 설정
	    if (userType.equals("일반회원")) {
	        user.setApprovalStatus(1); // 일반회원
	    } else if (userType.equals("사업자")) {
	        user.setApprovalStatus(2); // 승인 대기 중인 사업자
	    }

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
   //다른페이지에서도 네브바 프로필사진과 닉네임이 뜨게 하기 위해 작성 
   public Optional<SiteUser> getUserByUsername(String username) {
	    return userRepository.findByUsername(username);
	}


   public boolean checkPassword(String rawPassword, String encodedPassword) {
      return passwordEncoder.matches(rawPassword, encodedPassword);
   }
   
   // 유저 아이디 찾기
   public Optional<SiteUser> getUserById(Long id) {
      return userRepository.findById(id);
   }

// 닉네임 중복 확인 로직
	public boolean isNicknameTaken(String nickname) {
		return userRepository.findByNickname(nickname).isPresent();
	}
	
	public List<SiteUser> findAllUsers() {
	    return userRepository.findAll(); // DB에서 모든 사용자 조회
	}
	
	 // 회원 삭제
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);  // JPA Repository의 deleteById 메서드를 사용하여 삭제
    }
    
    public Page<SiteUser> searchById(String id, Pageable pageable) {
        try {
            Long userId = Long.parseLong(id);
            return userRepository.findAllById(userId, pageable);
        } catch (NumberFormatException e) {
            return Page.empty(pageable);
        }
    }


    public Page<SiteUser> searchByUsername(String username, Pageable pageable) {
        return userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    }

    public Page<SiteUser> searchByNickname(String nickname, Pageable pageable) {
        return userRepository.findByNicknameContainingIgnoreCase(nickname, pageable);
    }

    public Page<SiteUser> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);  // 모든 유저 정보를 페이지네이션으로 반환
    }

	public Page<SiteUser> searchById(long long1, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

}

