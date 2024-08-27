package com.test.project;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {

	   @GetMapping("/index")
	    public String showIndexPage(Model model) {
	       
	        return "index"; 
	    }
	}