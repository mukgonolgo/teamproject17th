package com.test.project;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainContorller {
	@GetMapping("/")
	public String index() {
		return "index";
	}
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	

}
