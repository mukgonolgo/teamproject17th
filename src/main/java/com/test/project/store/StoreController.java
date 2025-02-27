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
		List<Store> storeList = storeService.getAllStore(); // 모든 가게를 가져옴
		model.addAttribute("storeList", storeList);
		return "store/store_alist"; // store_alist.html 파일로 이동
	}

	// 가게 리스트 표시
	// 가게 리스트 표시 및 검색 기능 추가
	@GetMapping("/list")
	public String getStores(@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "region", required = false) String region, Model model,
			Principal principal) {

		List<Store> storeList;

		// 지역이 선택된 경우
		if (region != null && !region.isEmpty()) {
			storeList = storeService.getStoreByRegion(region); // 지역에 따른 가게 검색
		} else if (query != null && !query.isEmpty()) {
			storeList = storeService.searchStore(query); // 검색어에 따른 가게 검색
		} else {
			storeList = storeService.getAllStore(); // 전체 가게 가져오기
		}

		// 각 가게의 주소를 잘라서 설정
		for (Store store : storeList) {
			String shortAddress = getShortAddress(store.getBasicAddress());
			store.setBasicAddress(shortAddress); // 잘린 주소 설정
			// 현재 사용자가 좋아요를 눌렀는지 확인
			if (principal != null) {
				SiteUser siteUser = userService.getUser(principal.getName());
				boolean hasVoted = store.getVoter().contains(siteUser);
				model.addAttribute("hasVoted", hasVoted);
			} else {
				model.addAttribute("hasVoted", false); // 로그인하지 않은 경우
			}

		}

		model.addAttribute("stores", storeList); // 가게 리스트 추가
		return "store/store_list"; // 리스트 페이지 템플릿
	}

	private String getShortAddress(String address) {
		int endIndex = address.indexOf("시");
		if (endIndex == -1) { // "시"가 없다면 "구"를 찾음
			endIndex = address.indexOf("구");
		}
		if (endIndex != -1) {
			return address.substring(0, endIndex + 1); // "시"나 "구"까지 포함해서 자름
		}
		return address; // "시"나 "구"가 없으면 전체 주소 반환
	}

	// 가게 등록 폼
	@GetMapping("/create")
	public String showAddStoreForm() {
		return "store/store_form"; // 가게 등록 폼 템플릿
	}

	// 가게 등록 처리
	@PostMapping("/create")
	public String createStore(@RequestParam("storeName") String storeName, @RequestParam("postcode") String postcode,
			@RequestParam("basicAddress") String basicAddress, @RequestParam("detailAddress") String detailAddress,
			@RequestParam("storeLatitude") double storeLatitude, @RequestParam("storeLongitude") double storeLongitude,
			@RequestParam("storeContent") String storeContent, @RequestParam("kategorieInput") String kategorieGroup,
			@RequestParam("tagInput") String storeTagGroups, @RequestParam("storeNumber") String storeNumber,
			@RequestParam("StoreStarttime") String storeStarttime, @RequestParam("StoreEndTime") String storeEndTime,
			@RequestParam(value = "storeAdvertisement", required = false) Boolean storeAdvertisement,
			@RequestParam(value = "isPremium", defaultValue = "false") boolean isPremium,
			@RequestParam("imageFile") MultipartFile imageFile, @AuthenticationPrincipal UserDetails userDetails)
			throws IOException {
		if (userDetails == null) {
			return "redirect:/login"; // 인증되지 않은 경우 로그인 페이지로 리다이렉트
		}

		// 로그인한 사용자 정보 (사업자 정보)
		SiteUser siteUser = userService.getUser(userDetails.getUsername());

		// 가게 정보 저장
		Store store = storeService.saveStore(storeName, postcode, basicAddress, detailAddress, storeLatitude,
				storeLongitude, storeContent, kategorieGroup, storeTagGroups, storeNumber, storeStarttime, storeEndTime,
				storeAdvertisement, siteUser, isPremium, imageFile);

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
	public String detail(Model model, @PathVariable("storeId") Integer storeId, Principal principal) {
		Store store = storeService.getStore(storeId);
		model.addAttribute("store", store);

//        // SiteUser의 username을 모델에 추가 에러 발생
		if (store.getSiteUser() != null) {
			model.addAttribute("username", store.getSiteUser().getUsername());
			model.addAttribute("storeImage", store.getImageUrl());
		}
		// 평균 평점 및 리뷰 수 계산
		double averageRating = storeService.getStoreForstar(storeId);
		model.addAttribute("averageRating", averageRating);

		// 현재 사용자가 좋아요를 눌렀는지 확인
		if (principal != null) {
			SiteUser siteUser = userService.getUser(principal.getName());
			boolean hasVoted = store.getVoter().contains(siteUser);
			model.addAttribute("hasVoted", hasVoted);
		} else {
			model.addAttribute("hasVoted", false); // 로그인하지 않은 경우
		}

		return "store/store_detail"; // 상세 페이지 템플릿
	}

	// 좋아요 클릭시
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
	public String approveStore(@RequestParam("storeId") Integer storeId, @RequestParam("page") int page,
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
	public String holdStore(@RequestParam("storeId") Integer storeId, @RequestParam("page") int page,
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
		storeService.deleteStore(storeId); // 가게 삭제 로직 호출
		return "redirect:/store/store_alist"; // 삭제 후 리스트로 리디렉션
	}

}