package com.test.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Controller
public class StoreController {
	
	@GetMapping("/store")
	public String store_list() {
		return "store/store_list";
	}
	
	@GetMapping("/store/detail")
	public String store_detail() {
		return "store/store_detail";
	}
    @Autowired
    private StoreService storeService;

    @GetMapping("/posts")
    public String getPosts(Model model) {
        model.addAttribute("test", storeService.findAll());
        return "test";
    }
	
}

