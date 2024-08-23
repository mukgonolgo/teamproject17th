package com.test.project;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainComtorller {
	@GetMapping("/")
	@ResponseBody
	public String index() {
		return "현체 페이지 출력";
	}
}
