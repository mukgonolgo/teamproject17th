package com.test.project;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;

    @ModelAttribute
    public void addCommonAttributes(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            userService.getUserByUsername(userDetails.getUsername())
                .ifPresent(user -> {
                    model.addAttribute("profileImage", user.getImageUrl());
                    model.addAttribute("username", user.getUsername());
                    model.addAttribute("userid",user.getId());
                });
        }
    }
}
