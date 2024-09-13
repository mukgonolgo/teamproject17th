package com.test.project;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Controller
public class MainController {

	private final UserService userService;
	
	
	@GetMapping("/")
	public String root(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if(userDetails != null) {
			SiteUser user = userService.getUser(userDetails.getUsername());
			model.addAttribute("profileImage",user.getImageUrl());
			model.addAttribute("username",user.getUsername());
			model.addAttribute("userId",user.getId());
		}
		return "index";
	}





	
}

