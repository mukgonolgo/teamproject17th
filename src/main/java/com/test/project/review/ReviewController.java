package com.test.project.review;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ReviewController {
	//리뷰페이지
	@GetMapping("/review")
	public String review_page() {
		return "review/review_page";
	}
	
	//리뷰 상세페이지
	@GetMapping("/detail")
	public String review_detail() {
		return "review/review_detail";
	}
	
	//리뷰 피드 페이지
	@GetMapping("/review_feed")
	public String review_feed() {
		return "review/review_feed";
	}
	
}