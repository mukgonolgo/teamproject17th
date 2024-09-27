package com.test.project;

import java.util.List;
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

import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.like.ReviewLikeService;
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
	   
	@GetMapping("/")
	public String root(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		if(userDetails != null) {
			SiteUser user = userService.getUser(userDetails.getUsername());

			model.addAttribute("profileImage",user.getImageUrl());
			model.addAttribute("username",user.getUsername());
			model.addAttribute("nickname",user.getNickname());
			model.addAttribute("userid",user.getId());

		}
		return "index";
	}
	
	@GetMapping("/mypage/{userid}")
	public String mypage(@PathVariable("userid") Long userid, Model model) {
	    Optional<SiteUser> user = userService.getUserById(userid);
	    
	    if (user.isPresent()) {
	        SiteUser siteUser = user.get();
	        model.addAttribute("user", siteUser);  // 전체 유저 객체 추가

	        // 좋아요한 리뷰의 첫 번째 이미지를 가져옴
	        List<ReviewImageMap> likedImages = reviewLikeService.getFirstImagesForLikedReviews(userid);
	        model.addAttribute("likedImages", likedImages);  // 좋아요한 이미지 리스트 추가

	        return "user/mypage";  // mypage.html 파일을 렌더링
	    }

	    return "redirect:/error";  // 유저가 없으면 에러 처리
	}


	

}

