package com.test.project.store;

import java.util.List;

import org.h2.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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
    public String showAddstoreForm() {
    return "store/store_form";
    }

    @PostMapping("/store/create")
    public String createStore(
            @RequestParam(value = "storeName") String storeName,
            @RequestParam(value = "postcode") String postcode,
            @RequestParam(value = "basicAddress") String basicAddress,
            @RequestParam(value = "basicAddress") String detilAddress,
            @RequestParam(value = "storeLatitude") double storeLatitude,
            @RequestParam(value = "storeLongitude") double storeLongitude,
    		@RequestParam(value = "storeContent") String storeContent,
    		@RequestParam(value = "kategorieGroup") String kategorieGroup,
    		@RequestParam(value = "storeTagGroups") String storeTagGroups,
    		@RequestParam(value = "storeNumber") String storeNumber,
    		@RequestParam(value = "StoreStarttime") String StoreStarttime,
    		@RequestParam(value = "StoreEndTime") String StoreEndTime,
    		@RequestParam(value = "storeAdvertisement") boolean storeAdvertisement){


        // StoreService의 saveStore 메서드 호출
        storeService.saveStore(storeName,postcode,basicAddress,detilAddress,storeLatitude,storeLatitude,storeContent
        	,kategorieGroup,storeTagGroups,storeNumber,StoreStarttime,StoreEndTime	);

        return "redirect:/stores/store_detail"; // 저장 후 이동할 경로
    }


    @GetMapping("/detail/{StoreId}")
    public String detail(Model model, @PathVariable("StoreId") Integer StoreId) {
        Store store = this.storeService.getStore(StoreId);
        model.addAttribute("store", store);
        return "store/store_detail";
    }


	
}

