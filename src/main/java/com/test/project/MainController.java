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
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.project.reservation.Reservation;
import com.test.project.reservation.ReservationService;

import com.test.project.review.Review;
import com.test.project.review.ReviewService;

import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
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
	        List<Store> storeList = storeService.getTopStores(6);  // StoreService에 getTopStores 메서드를 추가
	        model.addAttribute("storeList", storeList);

	        // 최신 리뷰 6개 가져오기
	        List<Review> recentReviews = reviewService.getRecentReviews(6);  // 이미 구현된 메서드
	        model.addAttribute("recentReviews", recentReviews);

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
	            Long likeCount = reviewLikeService.countLikes(reviewId);  // 좋아요 수 가져오기

	            if (userId != null) {
	                likedByUser = reviewLikeService.isLikedByUser(reviewId, userId);  // 사용자가 좋아요를 눌렀는지 확인
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

	        return "index";  // index.html 파일을 렌더링
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
           

	          return "user/mypage"; // mypage.html 파일을 렌더링
	       }

	       return "redirect:/error"; // 유저가 없으면 에러 처리
	    }

	

}

