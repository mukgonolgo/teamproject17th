package com.test.project.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.List;
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

	// UserController의 signup 메서드에서 오류가 있을 때, 오류 메시지를 모델에 추가
	@PostMapping("/signup")
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult,
	                     @RequestParam(value = "imageFile", required = false) MultipartFile imageFile, Model model) {
	    if (bindingResult.hasErrors()) {
	        // 오류 처리...
	        return "user/signup_form";
	    }

	    if (!userCreateForm.getPassword().equals(userCreateForm.getConfirmPassword())) {
	        model.addAttribute("errorMessage", "패스워드를 일치시켜 주세요.");
	        return "user/signup_form";
	    }

	    try {
	        userService.create(userCreateForm.getUsername(), userCreateForm.getEmailDomain(),
	                           userCreateForm.getPassword(), userCreateForm.getPostcode(),
	                           userCreateForm.getBasicAddress(), userCreateForm.getDetailAddress(),
	                           userCreateForm.getContact(), userCreateForm.getSnsAgree(), imageFile,
	                           userCreateForm.getName(), userCreateForm.getNickname(), userCreateForm.getUserType());
	    } catch (DataIntegrityViolationException e) {
	        model.addAttribute("errorMessage", "이미 등록된 사용자입니다.");
	        return "user/signup_form";
	    } catch (Exception e) {
	        model.addAttribute("errorMessage", e.getMessage());
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

	
	// 닉네임 중복 체크
	@GetMapping("/checkNickname")
	@ResponseBody
	public String checkNickname(@RequestParam("nickname") String nickname) {
	    try {
	        boolean isTaken = userService.isNicknameTaken(nickname);
	        return "{\"isTaken\": " + isTaken + "}";
	    } catch (Exception e) {
	        return "{\"error\": \"서버 오류가 발생했습니다.\"}";
	    }
	}
	
	@GetMapping("/user/showList")
	public String showUserList(Model model) {
	    List<SiteUser> userList = userService.findAllUsers(); // 모든 사용자 정보 가져오기
	    model.addAttribute("userList", userList);
	    return "user/user_list"; // user_list.html 페이지로 이동
	}
	
	@PostMapping("/approve")
	public String approveUser(@RequestParam("userId") Long userId,
	                          @RequestParam("approvalStatus") int approvalStatus,
	                          @RequestParam("page") int currentPage,
	                          @RequestParam("search") String search) {
	    Optional<SiteUser> userOptional = userRepository.findById(userId);
	    if (userOptional.isPresent()) {
	        SiteUser user = userOptional.get();
	        user.setApprovalStatus(approvalStatus);
	        userRepository.save(user);
	    }
	    // 현재 페이지 번호를 리다이렉션에 포함시킴
	    return "redirect:/user/list?page=" + currentPage + "&search=" + search;
	}



	@GetMapping("/somePage")
	public String somePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	    if (userDetails != null) {
	        SiteUser user = userService.getUser(userDetails.getUsername());
	        model.addAttribute("approvalStatus", user.getApprovalStatus());
	        model.addAttribute("profileImage", user.getImageUrl());
	    }
	    return "some_template";
	}
	@PostMapping("/hold")
	public String holdUser(@RequestParam("userId") Long userId,
	                       @RequestParam("approvalStatus") int approvalStatus,
	                       @RequestParam("page") int currentPage,
	                       @RequestParam("search") String search) {
	    Optional<SiteUser> userOptional = userRepository.findById(userId);
	    if (userOptional.isPresent()) {
	        SiteUser user = userOptional.get();
	        user.setApprovalStatus(approvalStatus);
	        userRepository.save(user);
	    }
	    // 현재 페이지 번호를 리다이렉션에 포함시킴
	    return "redirect:/user/list?page=" + currentPage + "&search=" + search;
	}

	
	// 재심사 요청 처리 (POST)
	@PostMapping("/reapply")
	public String reapplyForApproval(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
	    try {
	        // 사용자 상태를 승인 대기로 변경
	        SiteUser user = userService.getUser(userDetails.getUsername());
	        user.setApprovalStatus(2);  // 승인 대기 상태로 변경
	        userRepository.save(user);

	        // 사용자 세션을 갱신하여 새로운 승인 상태 반영
	        CustomUserDetails updatedUserDetails = new CustomUserDetails(user, userDetails.getAuthorities());  // SiteUser와 기존 권한을 사용하여 새로운 CustomUserDetails 생성
	        UsernamePasswordAuthenticationToken authentication = 
	            new UsernamePasswordAuthenticationToken(updatedUserDetails, null, updatedUserDetails.getAuthorities());
	        
	        SecurityContextHolder.getContext().setAuthentication(authentication);  // 세션 갱신

	        // 성공 메시지 추가
	        model.addAttribute("successMessage", "재심사 요청이 성공적으로 처리되었습니다.");
	    } catch (Exception e) {
	        // 오류 발생 시 메시지 추가
	        model.addAttribute("errorMessage", "재심사 요청 중 오류가 발생했습니다.");
	        return "user/profile";  // 오류 발생 시 프로필 페이지로 돌아감
	    }

	    // 재심사 요청 후 index.html로 리디렉션
	    return "redirect:/";
	}
	
	  // 회원 삭제 처리
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);  // 서비스에서 삭제 로직 처리
        return "redirect:/user/list";    // 삭제 후 다시 회원 리스트 페이지로 리디렉션
    }
    
    @GetMapping("/list")
    public String getUserList(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "searchType", defaultValue = "id") String searchType,
                              Model model) {
        // 페이지 요청을 ID 필드의 내림차순으로 정렬 (추가됨)
    	Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")); // ID로 오름차순 정렬
        Page<SiteUser> userPage = null;

        // 검색 처리 (추가)
        if (search != null && !search.isEmpty()) {
            switch (searchType) {
                case "id":
                    userPage = userService.searchById(Long.parseLong(search), pageable);
                    break;
                case "username":
                    userPage = userService.searchByUsername(search, pageable);
                    break;
                case "nickname":
                    userPage = userService.searchByNickname(search, pageable);
                    break;
                default:
                    userPage = userService.getAllUsers(pageable);
                    break;
            }
        } else {
            userPage = userService.getAllUsers(pageable);
        }

        model.addAttribute("userPage", userPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("searchType", searchType);

        return "user/user_list";  // user_list.html 반환
    }



	}










