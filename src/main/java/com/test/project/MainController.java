package com.test.project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.project.reservation.Reservation;
import com.test.project.reservation.ReservationService;
import com.test.project.review.Review;
import com.test.project.review.ReviewService;
import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.like.LikeStatusDto;
import com.test.project.review.like.ReviewLikeService;
import com.test.project.store.Store;
import com.test.project.store.StoreService;
import com.test.project.user.SiteUser;
import com.test.project.user.UserRepository;
import com.test.project.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MainController {

	@Autowired
	private final ReviewLikeService reviewLikeService;
	private final UserService userService;
	private final ReviewService reviewService;

	private final ReservationService reservationService;

	@Autowired
	private StoreService storeService;

	@GetMapping("/")
	public String root(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		// 가게 목록 6개 가져오기
		List<Store> storeList = storeService.getTopStores(6);
		model.addAttribute("storeList", storeList); // 가게 목록은 전체 주소 그대로 유지

		// 최신 리뷰 6개 가져오기
		List<Review> recentReviews = reviewService.getRecentReviews(6);

		// 리뷰의 주소는 '시'나 '구'까지만 잘라서 표시할 수 있도록 추가 데이터를 모델에 담기
		Map<Long, String> shortAddressMap = new HashMap<>(); // 리뷰별 짧은 주소 저장

		for (Review review : recentReviews) {
			Store store = review.getStore();
			if (store != null) {
				String shortAddress = getShortAddress(store.getBasicAddress()); // '시'나 '구'까지 자르기
				shortAddressMap.put(review.getId(), shortAddress); // 리뷰 ID를 키로 하고 짧은 주소 저장
			}
		}

		// 모델에 리뷰와 주소 정보를 함께 전달
		model.addAttribute("recentReviews", recentReviews);
		model.addAttribute("shortAddressMap", shortAddressMap); // 리뷰용 짧은 주소 추가

		// 현재 로그인한 사용자 정보 처리
		Long userId = null;
		if (userDetails != null) {
			SiteUser user = userService.getUser(userDetails.getUsername());
			userId = user.getId();
			model.addAttribute("profileImage", user.getImageUrl());
			model.addAttribute("username", user.getUsername());
			model.addAttribute("nickname", user.getNickname());
			model.addAttribute("userid", userId);
		} else {
			// 로그인하지 않은 경우 처리
			model.addAttribute("profileImage", "/img/user/default-profile.png");
		}

		// 좋아요 상태 및 댓글 수를 저장할 맵
		Map<Long, LikeStatusDto> likeStatusMap = new HashMap<>();
		Map<Long, Long> commentCountMap = new HashMap<>();

		// 각 리뷰에 대해 좋아요 상태와 댓글 수 계산
		for (Review review : recentReviews) {
			Long reviewId = review.getId();
			boolean likedByUser = false;
			Long likeCount = reviewLikeService.countLikes(reviewId); // 좋아요 수 가져오기

			if (userId != null) {
				likedByUser = reviewLikeService.isLikedByUser(reviewId, userId); // 사용자가 좋아요를 눌렀는지 확인
			}

			LikeStatusDto likeStatusDto = new LikeStatusDto(likedByUser, likeCount.intValue());
			likeStatusMap.put(reviewId, likeStatusDto);

			// 댓글 수 계산
			long commentCount = reviewService.getCommentCountForReview(reviewId);
			commentCountMap.put(reviewId, commentCount);
		}

		// 모델에 좋아요 상태 및 댓글 수 정보 추가
		model.addAttribute("likeStatusMap", likeStatusMap);
		model.addAttribute("commentCountMap", commentCountMap);

		return "index"; // index.html 파일을 렌더링
	}

	// 주소를 "시" 또는 "구"까지 자르는 메서드 추가
	private String getShortAddress(String address) {
		// "구" 또는 "시"까지 자르기
		int endIndex = address.indexOf("구");
		if (endIndex == -1) { // 구가 없으면 시까지 자르기
			endIndex = address.indexOf("시");
		}
		if (endIndex != -1) {
			return address.substring(0, endIndex + 1); // 구 또는 시까지 포함해서 자르기
		}
		return address; // 만약 "구"나 "시"가 없다면 전체 주소 반환
	}

	@GetMapping("/mypage/{userid}")
	public String mypage(@PathVariable("userid") Long userid, Model model) {
		Optional<SiteUser> user = userService.getUserById(userid);

		if (user.isPresent()) {
			SiteUser siteUser = user.get();
			model.addAttribute("user", siteUser); // 전체 유저 객체 추가
			List<Reservation> reservations = reservationService.getReservationsByUser(siteUser);
			model.addAttribute("reservations", reservations);

			// 좋아요한 리뷰의 첫 번째 이미지를 가져옴
			List<ReviewImageMap> likedImages = reviewLikeService.getFirstImagesForLikedReviews(userid);
			model.addAttribute("likedImages", likedImages); // 좋아요한 이미지 리스트 추가

			
			   // 좋아요를 누른 식당 리스트 가져오기
	         List<Store> likedStores = storeService.getStoresLikedByUser(siteUser); // 사용자에 의해 좋아요가 눌린 가게 리스트
	         model.addAttribute("likedStores", likedStores); // 좋아요한 식당 리스트 추가

			
			 // approvalStatus가 5인 랜덤 가게 가져오기
	           Optional<Store> randomStore = storeService.getRandomStoreWithApprovalStatus5();
	           randomStore.ifPresent(store -> model.addAttribute("randomStore", store));

			
			return "user/mypage"; // mypage.html 파일을 렌더링
		}

		return "redirect:/error"; // 유저가 없으면 에러 처리
	}

	@GetMapping("/search")
	public String search(@RequestParam("keyword") String keyword, Model model) {
	    // 리뷰와 식당 검색 결과
	    List<Review> reviewResults = reviewService.searchReviewsByKeyword(keyword);
	    List<Store> storeResults = storeService.searchStoresByKeyword(keyword);

	    // 검색 결과를 모델에 추가
	    model.addAttribute("reviewResults", reviewResults);
	    model.addAttribute("storeResults", storeResults);
	    model.addAttribute("keyword", keyword);  // 검색어도 모델에 추가

	    return "index"; // index.html로 반환하여 같은 페이지에서 결과를 보여줌
	}

	
	public List<Review> highlightReviews(List<Review> reviews, String keyword) {
	    for (Review review : reviews) {
	        if (review.getTitle() != null && review.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
	            review.setTitle(review.getTitle().replaceAll("(?i)(" + keyword + ")", "<mark>$1</mark>"));
	        }
	        if (review.getContent() != null && review.getContent().toLowerCase().contains(keyword.toLowerCase())) {
	            review.setContent(review.getContent().replaceAll("(?i)(" + keyword + ")", "<mark>$1</mark>"));
	        }
	        if (review.getStore() != null && review.getStore().getStoreName().toLowerCase().contains(keyword.toLowerCase())) {
	            review.getStore().setStoreName(review.getStore().getStoreName().replaceAll("(?i)(" + keyword + ")", "<mark>$1</mark>"));
	        }
	    }
	    return reviews;
	}



}
