package com.test.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequestMapping("/store")
@RequiredArgsConstructor
@Controller
public class StoreController {
    private final StoreService storeService;

    // 가게 리스트 표시
    @GetMapping("/list")
    public String getStores(Model model) {
        List<Store> storeList = storeService.getAllStore();
        model.addAttribute("storeList", storeList);
        return "store/store_list"; // 리스트 페이지 템플릿
    }

    // 가게 등록 폼
    @GetMapping("/create")
    public String showAddStoreForm() {
        return "store/store_form"; // 가게 등록 폼 템플릿
    }

    // 가게 등록 처리
    @PostMapping("/create")
    public String createStore(
        @RequestParam("storeName") String storeName,
        @RequestParam("postcode") String postcode,
        @RequestParam("basicAddress") String basicAddress,
        @RequestParam("detailAddress") String detailAddress,
        @RequestParam("storeLatitude") double storeLatitude,
        @RequestParam("storeLongitude") double storeLongitude,
        @RequestParam("storeContent") String storeContent,
        @RequestParam("kategorieInput") String kategorieGroup,
        @RequestParam("tagInput") String storeTagGroups,
        @RequestParam("storeNumber") String storeNumber,
        @RequestParam("StoreStarttime") String storeStarttime,
        @RequestParam("StoreEndTime") String storeEndTime,
        @RequestParam(value = "storeAdvertisement", required = false) Boolean storeAdvertisement
    ) {
        // 저장 처리
        Store store = storeService.saveStore(storeName, postcode, basicAddress, detailAddress, storeLatitude, storeLongitude,
                storeContent, kategorieGroup, storeTagGroups, storeNumber, storeStarttime, storeEndTime, storeAdvertisement);
        // 등록 후 상세 페이지로 리다이렉트
        return "redirect:/store/detail/" + store.getStoreId();
    }

    // 가게 상세 페이지
    @GetMapping("/detail/{storeId}")
    public String detail(Model model, @PathVariable("storeId") Integer storeId) {
        Store store = storeService.getStore(storeId);
        model.addAttribute("store", store);
        return "store/store_detail"; // 상세 페이지 템플릿
    }
}
