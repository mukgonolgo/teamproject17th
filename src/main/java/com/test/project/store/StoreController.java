package com.test.project.store;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;


import lombok.RequiredArgsConstructor;

@RequestMapping("/store")
@RequiredArgsConstructor
@Controller
public class StoreController {
    private final StoreService storeService;
    private final UserService userService;

    // 이미지 업로드 기본 경로 설정 kimssam
    private final String uploadDir = "C:/upload/";
    
    
    @GetMapping("/store_alist_full")
    public String getStoreListForAdminWithoutPagination(Model model) {
        List<Store> storeList = storeService.getAllStore();  // 모든 가게를 가져옴
        model.addAttribute("storeList", storeList);
        return "store/store_alist";  // store_alist.html 파일로 이동
    }

    // 가게 리스트 표시
 // 가게 리스트 표시 및 검색 기능 추가
    @GetMapping("/list")
    public String getStores(
        @RequestParam(value = "search", required = false) String search,
        @RequestParam(value = "searchType", defaultValue = "storeName") String searchType,
        Model model) {

        List<Store> storeList;

        // 검색어가 있는 경우
        if (search != null && !search.isEmpty()) {
            if ("storeName".equals(searchType)) {
                storeList = storeService.searchStoresByStoreName(search);
            } else if ("basicAddress".equals(searchType)) {
                storeList = storeService.searchStoresByBasicAddress(search);
            } else {
                storeList = storeService.getAllStore(); // 검색어가 없거나 검색 유형이 일치하지 않는 경우 전체 조회
            }
        } else {
            storeList = storeService.getAllStore(); // 검색어가 없으면 전체 조회
        }

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
        @RequestParam("imageFile") MultipartFile imageFile,
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
            storeNumber, storeStarttime, storeEndTime, storeAdvertisement, siteUser, isPremium,imageFile);
        
        // 이미지 파일 저장 처리 kimssam
        String imageUrl = null;
        if (!imageFile.isEmpty()) {
            // 파일 저장 경로 설정
            String originalFileName = imageFile.getOriginalFilename();
            Path imagePath = Paths.get(uploadDir + originalFileName);

            // 디렉토리 생성 (존재하지 않을 경우)
            if (!Files.exists(imagePath.getParent())) {
                Files.createDirectories(imagePath.getParent());
            }

            // 파일 저장
            Files.write(imagePath, imageFile.getBytes());
            imageUrl = "/uploads/" + originalFileName; // 나중에 정적 자원 경로에서 접근할 수 있도록 경로 설정
        }


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
            model.addAttribute("storeImage",store.getImageUrl());
        }
        // 평균 평점 및 리뷰 수 계산
        double averageRating = storeService.getStoreForstar(storeId);
        model.addAttribute("averageRating", averageRating);


        return "store/store_detail"; // 상세 페이지 템플릿
    }
    
  //좋아요 클릭시	
//    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{storeId}")
    public String storeVote(Principal principal, @PathVariable("storeId") Integer storeId) {
    	Store store = this.storeService.getStore(storeId);
    	SiteUser siteUser = this.userService.getUser(principal.getName());
        // 서비스 메서드 호출
        this.storeService.vote(store, siteUser);
    	return String.format("redirect:/store/detail/%s", storeId);
    	
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
    @PreAuthorize("hasRole('ADMIN')")  // 관리자 권한이 있는 사용자만 접근 가능
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