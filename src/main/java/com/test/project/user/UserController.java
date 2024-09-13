package com.test.project.user;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/user")
@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            SiteUser user = userService.getUser(userDetails.getUsername());
            model.addAttribute("profileImage", user.getImageUrl());
        }
        return "user/signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, @RequestParam MultipartFile imageFile) {
        if (bindingResult.hasErrors()) {
            return "user/signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 서로 일치하지 않습니다.");
            return "user/signup_form";
        }

        try {
            userService.create(
                userCreateForm.getUsername(),
                userCreateForm.getEmail(),
                userCreateForm.getPassword1(),
                userCreateForm.getAddress(),
                userCreateForm.getPhoneNumber(),
                imageFile
            );

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "user/signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "user/signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername()); // username을 모델에 추가
            System.out.printf("user name = %s%n" , userDetails.getUsername());
        } else {
            System.out.println("No userDetails available.");
        }
        return "user/login_form";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, HttpSession session) {
        // 사용자 ID를 세션에 저장
        SiteUser user = userService.getUser(username); // 사용자의 ID를 얻기 위해 사용자 객체 가져오기
        if (user != null) {
            session.setAttribute("userId", user.getId()); // 사용자 ID를 세션에 저장
        }
        return "redirect:/chat";
    }
}
