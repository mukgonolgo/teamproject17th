package com.test.project.store;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import lombok.RequiredArgsConstructor;



@RequestMapping("/store")
@RequiredArgsConstructor
@Controller
public class StoreController {
	private final StoreService storeService;
	private final UserService userService;
	
	@GetMapping("/list")
    public String getStores(Model model) {
		List<Store> store = storeService.getAllStore();
        model.addAttribute("store", store);
        return "store/store_list";
	}
    @GetMapping("/create")
    public String showAddNoticeForm() {
        return "store/store_form";
    }

	
	@GetMapping("/detail/{StoreId}")
	public String detail(Model model, @PathVariable("userid") Integer StoreId, @AuthenticationPrincipal UserDetails userDetails) { //Integer타입의 id 컬럼값과 연결하여 @PathVariable("변수명")으로 변경한다!!=>사용자 요청 url의 변수명으로 사용가능하다!
		
		if(userDetails != null) {
			SiteUser user = userService.getUser(userDetails.getUsername());
			model.addAttribute("profileImage",user.getImageUrl());
		}
		Store store = this.storeService.getStore(StoreId);
		model.addAttribute("store",store );
		return "store/store_detail";
	}



	
}

