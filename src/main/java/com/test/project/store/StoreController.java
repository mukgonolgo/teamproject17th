package com.test.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.review.Review;
import com.test.project.user.SiteUser;
import com.test.project.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

@RequestMapping("/store")
@RequiredArgsConstructor
@Controller
public class StoreController {
    private final StoreService storeService;
    private final UserService userService;

    
    @GetMapping("/store_alist_full")
    public String getStoreListForAdminWithoutPagination(Model model) {
        List<Store> storeList = storeService.getAllStore();  // 모든 가게를 가져옴
        model.addAttribute("storeList", storeList);
        return "store/store_alist";  // store_alist.html 파일로 이동
    }

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
        @RequestParam(value = "storeAdvertisement", required = false) Boolean storeAdvertisement,
        @RequestParam(value = "isPremium", defaultValue = "false") boolean isPremium,
        @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        if (userDetails == null) {
            return "redirect:/login"; // 인증되지 않은 경우 로그인 페이지로 리다이렉트
        }

        // 로그인한 사용자 정보 (사업자 정보)
        SiteUser siteUser = userService.getUser(userDetails.getUsername());

        // 가게 정보 저장
        Store store = storeService.saveStore(storeName, postcode, basicAddress, detailAddress, 
            storeLatitude, storeLongitude, storeContent, kategorieGroup, storeTagGroups, 
            storeNumber, storeStarttime, storeEndTime, storeAdvertisement, siteUser, isPremium);
        


        // 등록 후 상세 페이지로 리디렉션
        return "redirect:/store/detail/" + store.getStoreId();
    }

 // 가게 상세 페이지
    @GetMapping("/detail/{storeId}")
    public String detail(Model model, @PathVariable("storeId") Integer storeId) {
        Store store = storeService.getStore(storeId);
        model.addAttribute("store", store);
        
//        // SiteUser의 username을 모델에 추가 에러 발생
        if (store.getSiteUser() != null) {
            model.addAttribute("username", store.getSiteUser().getUsername());
        }
        // 평균 평점 및 리뷰 수 계산
        double averageRating = storeService.getStoreForstar(storeId);
        model.addAttribute("averageRating", averageRating);


        return "store/store_detail"; // 상세 페이지 템플릿
    }

    @PostMapping("/approve")
    public String approveStore(@RequestParam("storeId") Integer storeId, 
                               @RequestParam("page") int page, 
                               @RequestParam("search") String search) {
        Store store = storeService.getStore(storeId);
        if (store.getApprovalStatus() == 4) {
            store.setApprovalStatus(5); // 프리미엄 광고 승인
        } else {
            store.setApprovalStatus(2); // 일반 광고 승인
        }
        storeService.save(store);

        // 처리 후 리다이렉트 시 페이지와 검색어 유지
        return "redirect:/store/store_alist?page=" + page + "&search=" + search;
    }

    @PostMapping("/hold")
    public String holdStore(@RequestParam("storeId") Integer storeId, 
                            @RequestParam("page") int page, 
                            @RequestParam("search") String search) {
        Store store = storeService.getStore(storeId);
        if (store.getApprovalStatus() == 4) {
            store.setApprovalStatus(6); // 프리미엄 광고 보류
        } else {
            store.setApprovalStatus(3); // 일반 광고 보류
        }
        storeService.save(store);

        // 처리 후 리다이렉트 시 페이지와 검색어 유지
        return "redirect:/store/store_alist?page=" + page + "&search=" + search;
    }

    
    // 가게 삭제 처리
    @PostMapping("/delete")
    public String deleteStore(@RequestParam("storeId") Integer storeId) {
        storeService.deleteStore(storeId);  // 가게 삭제 로직 호출
        return "redirect:/store/store_alist";  // 삭제 후 리스트로 리디렉션
    }
    
    // 가게 리스트 표시 - 페이지네이션과 검색 추가
    @GetMapping("/store_alist")
    public String getStoreListForAdmin(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "searchType", defaultValue = "owner") String searchType, // 검색 유형 추가
        Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("storeName").ascending());
        Page<Store> storePage;

        // 검색 유형에 따른 검색 처리
        if (search != null && !search.isEmpty()) {
            switch (searchType) {
                case "storeId":
                    storePage = storeService.searchStoresByStoreId(search, pageable);
                    break;
                case "storeName":
                    storePage = storeService.searchStoresByStoreName(search, pageable);
                    break;
                default:
                    storePage = storeService.searchStoresByOwnerUsername(search, pageable);
                    break;
            }
        } else {
            storePage = storeService.getAllStores(pageable);
        }

        model.addAttribute("storePage", storePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", storePage.getTotalPages());
        model.addAttribute("search", search); // 검색어를 모델에 추가
        model.addAttribute("searchType", searchType); // 검색 유형을 모델에 추가

        return "store/store_alist";
    }


    
}
