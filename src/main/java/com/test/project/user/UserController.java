package com.test.project.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RequestMapping("/user")
@RequiredArgsConstructor
@Controller
public class UserController {

	private final UserService userService;
	private final StringRedisTemplate stringRedisTemplate;
	// UserRepository 필드 추가
	private final UserRepository userRepository;

	@Value("${spring.mail.username}")
	private String fromEmail;

	// 회원가입 폼
	@GetMapping("/signup")
	public String signup(UserCreateForm userCreateForm, @AuthenticationPrincipal UserDetails userDetails, Model model) {
		if (userDetails != null) {
			SiteUser user = userService.getUser(userDetails.getUsername());
			model.addAttribute("profileImage", user.getImageUrl());
		}
		return "user/signup_form";
	}

	// 회원가입 처리
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult,
	                     @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
	    if (bindingResult.hasErrors()) {
	        return "user/signup_form";
	    }

	    if (!userCreateForm.getPassword().equals(userCreateForm.getConfirmPassword())) {
	        bindingResult.rejectValue("confirmPassword", "passwordInCorrect", "2개의 패스워드가 서로 일치하지 않습니다.");
	        return "user/signup_form";
	    }

	    try {
	        userService.create(userCreateForm.getUsername(), userCreateForm.getEmailDomain(),
	                           userCreateForm.getPassword(), userCreateForm.getPostcode(),
	                           userCreateForm.getBasicAddress(), userCreateForm.getDetailAddress(),
	                           userCreateForm.getContact(), userCreateForm.getSnsAgree(), imageFile, userCreateForm.getName());
	    } catch (DataIntegrityViolationException e) {
	        bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
	        return "user/signup_form";
	    } catch (Exception e) {
	        bindingResult.reject("signupFailed", e.getMessage());
	        return "user/signup_form";
	    }

	    return "redirect:/";
	}


	// 로그인 폼
	@GetMapping("/login")
	public String login() {
		return "user/login_form";
	}

	// 아이디 중복 체크
	@GetMapping("/checkUsername")
	@ResponseBody
	public String checkUsername(@RequestParam("username") String username) {
		try {
			boolean isTaken = userService.isUsernameTaken(username);
			return "{\"isTaken\": " + isTaken + "}";
		} catch (Exception e) {
			return "{\"error\": \"서버 오류가 발생했습니다.\"}";
		}
	}

	// 아이디 찾기 페이지
	@GetMapping("/findId")
	public String findIdForm() {
		return "user/find_id";
	}

	// 아이디 찾기 처리
	@PostMapping("/findId")
	public String findId(@RequestParam("name") String name, @RequestParam("email") String email, Model model) {
		String foundId = userService.findIdByNameAndEmail(name, email);
		if (foundId != null) {
			model.addAttribute("foundId", foundId);
			return "user/find_id_result";
		} else {
			model.addAttribute("error", "해당 정보로 아이디를 찾을 수 없습니다.");
			return "user/find_id";
		}
	}

	// 비밀번호 찾기 페이지
	@GetMapping("/findPassword")
	public String findPasswordForm() {
		return "user/find_password";
	}

	// 비밀번호 찾기에서 이메일 인증번호 발송
	@PostMapping("/sendAuthCode")
	@ResponseBody
	public String sendAuthCode(@RequestParam("email") String email) {
		Random random = new Random();
		String authCode = String.format("%06d", random.nextInt(1000000));

		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		valueOperations.set("authCode:" + email, authCode, 5, TimeUnit.MINUTES);

		try {
			userService.sendEmail(email, "비밀번호 찾기 인증번호", "인증번호는 " + authCode + " 입니다.");
		} catch (Exception e) {
			return "fail";
		}

		return "success";
	}

	// 인증번호 확인 및 Redis에 저장
	@PostMapping("/verifyAuthCode")
	@ResponseBody
	public String verifyAuthCode(@RequestParam("email") String email, @RequestParam("authCode") String authCode) {
		ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
		String storedAuthCode = valueOperations.get("authCode:" + email);

		if (storedAuthCode != null && storedAuthCode.equals(authCode)) {
			valueOperations.set("authVerified:" + email, "true", 10, TimeUnit.MINUTES); // 인증 상태 저장
			return "success";
		} else {
			return "fail";
		}
	}

	@PostMapping("/findPassword")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> findPassword(@RequestParam("name") String name,
			@RequestParam("username") String username) {
		Map<String, Object> result = new HashMap<>();

		// 공백 제거
		name = name.trim();
		username = username.trim();

		System.out.println("입력된 이름: " + name);
		System.out.println("입력된 아이디: " + username);

		// 이름 확인
		Optional<SiteUser> userByName = userRepository.findByName(name);
		System.out.println("이름으로 찾은 결과: " + userByName.isPresent());

		if (!userByName.isPresent()) {
			result.put("success", false);
			result.put("error", "이름이 일치하지 않습니다.");
			return ResponseEntity.ok(result);
		}

		// 아이디 확인
		Optional<SiteUser> userByUsername = userRepository.findByUsername(username);
		System.out.println("아이디로 찾은 결과: " + userByUsername.isPresent());

		if (!userByUsername.isPresent()) {
			result.put("success", false);
			result.put("error", "아이디가 일치하지 않습니다.");
			return ResponseEntity.ok(result);
		}

		// 이름과 아이디가 모두 일치하는지 확인
		Optional<SiteUser> userByNameAndUsername = userRepository.findByNameAndUsername(name, username);
		System.out.println("이름과 아이디로 찾은 결과: " + userByNameAndUsername.isPresent());

		if (!userByNameAndUsername.isPresent()) {
			result.put("success", false);
			result.put("error", "사용자 정보가 일치하지 않습니다.");
			return ResponseEntity.ok(result);
		}

		// 모든 조건이 일치하면 성공
		result.put("success", true);
		result.put("redirectUrl", "/user/reset_password?username=" + username); // username을 포함한 URL 반환
		return ResponseEntity.ok(result);
	}

	// 비밀번호 재설정 처리
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("confirmPassword") String confirmPassword, Model model) {
		System.out.println("비밀번호 재설정 요청된 아이디: " + username); // 로그로 username 확인

		if (!password.equals(confirmPassword)) {
			model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
			return "user/reset_password";
		}

		userService.resetPassword(username, password);
		return "redirect:/user/login";
	}

	@GetMapping("/reset_password")
	public String showResetPasswordForm(@RequestParam("username") String username, Model model) {
		model.addAttribute("username", username);
		return "user/reset_password"; // user/reset_password.html 파일을 반환
	}

	@GetMapping("/sign_member")
	public String signMemberForm(Model model) {
		model.addAttribute("userCreateForm", new UserCreateForm());
		return "user/sign_member"; // sign_member.html을 반환
	}
	// 내 정보 수정 페이지 표시
	@GetMapping("/profile")
	public String showProfile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	    if (userDetails != null) {
	        SiteUser user = userService.getUser(userDetails.getUsername());
	        UserUpdateForm userUpdateForm = new UserUpdateForm();
	        userUpdateForm.setName(user.getName());
	        userUpdateForm.setEmail(user.getEmail());
	        userUpdateForm.setPhoneNumber(user.getPhoneNumber());
	        
	     // 주소 관련 정보 설정
	        userUpdateForm.setPostcode(user.getPostcode());  // 우편번호
	        userUpdateForm.setBasicAddress(user.getBasicAddress());  // 기본 주소
	        userUpdateForm.setDetailAddress(user.getDetailAddress());  // 상세 주소
	        
	        model.addAttribute("userUpdateForm", userUpdateForm);
	    }
	    return "user/profile";
	}

	// 내 정보 수정 처리
	@PostMapping("/profile")
	public String updateProfile(@Valid @ModelAttribute("userUpdateForm") UserUpdateForm userUpdateForm, 
	                            BindingResult bindingResult, 
	                            @AuthenticationPrincipal UserDetails userDetails, 
	                            Model model) {
	    if (bindingResult.hasErrors()) {
	        return "user/profile";
	    }

	    try {
	        userService.updateUserProfile(userDetails.getUsername(), userUpdateForm);
	    } catch (Exception e) {
	        bindingResult.reject("profileUpdateFailed", e.getMessage());
	        return "user/profile";
	    }

	    return "redirect:/"; // 내 정보 수정 후 index.html로 리디렉션
	}

	// 비밀번호 확인 페이지
	@GetMapping("/confirmPassword")
	public String showConfirmPasswordPage() {
	    return "user/confirm";  // 비밀번호 확인 페이지로 이동
	}

	// 비밀번호 확인 처리
	@PostMapping("/confirmPassword")
	public String confirmPassword(@RequestParam("password") String password, 
	                              @AuthenticationPrincipal UserDetails userDetails, 
	                              Model model) {
	    SiteUser user = userService.getUser(userDetails.getUsername());

	    // 비밀번호가 일치하는지 확인
	    if (userService.checkPassword(password, user.getPassword())) {
	        return "redirect:/user/profile";  // 비밀번호 일치 시 내 정보 수정 페이지로 이동
	    } else {
	        model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
	        return "user/confirm";  // 비밀번호 불일치 시 다시 확인 페이지로 이동
	    }
	}

	@GetMapping("/check-login")
	public ResponseEntity<String> checkLogin(@AuthenticationPrincipal SiteUser user) {
	    if (user != null) {
	        return ResponseEntity.ok("Logged in as: " + user.getUsername());
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
	    }
	}



}
