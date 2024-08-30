package com.test.project;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainComtorller {
	@GetMapping("/")
	public String index() {
		return "index";
	}
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	@GetMapping("/review")
	public String review_page() {
		return "review_page";
	}
	@GetMapping("/review_detail")
	public String review_detail() {
		return "review_detail";
	}
	@GetMapping("/eat")
	public String eat() {
		return "eat_friends";	}
	@GetMapping("/review_feed")
	public String review_feed() {
		return "review_feed";
	}
	@GetMapping("/footer")
	public String bord() {
		return "footer";
	}
}
