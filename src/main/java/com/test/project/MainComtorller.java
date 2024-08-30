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
	
	//리뷰페이지
	@GetMapping("/review")
	public String review_page() {
		return "review_page";
	}
	
	//리뷰 상세페이지
	@GetMapping("/review_detail")
	public String review_detail() {
		return "review_detail";
	}
	
	//리뷰 피드 페이지
	@GetMapping("/review_feed")
	public String review_feed() {
		return "review_feed";
	}
	
	//밥친구 페이지
	@GetMapping("/eat")
	public String eat() {
		return "eat_friends";	
	}
	
	//에러페이지
	@GetMapping("/error_404")
	public String error_404() {
		return "/error_404";	
	}
	@GetMapping("/error_500")
	public String error_500() {
		return "/error_500";	
	}
	@GetMapping("/error_403")
	public String error_403() {
		return "/error_403";	
	}
	@GetMapping("/error_401")
	public String error_401() {
		return "/error_401";	
	}
	
	@GetMapping("/footer")
	public String bord() {
		return "footer";
	}
}
