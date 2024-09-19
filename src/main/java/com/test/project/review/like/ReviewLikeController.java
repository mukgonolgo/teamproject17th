package com.test.project.review.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewLikeController {
	   @Autowired
	    private ReviewLikeService reviewLikeService;

	    // 사용자가 특정 리뷰에 좋아요를 누르거나 취소하는 API
	    @PostMapping("/reviews/{reviewId}/like/{userId}")
	    public ResponseEntity<String> likeReview(@PathVariable Long reviewId, @PathVariable Long userId) {
	        reviewLikeService.likeReview(reviewId, userId);
	        return ResponseEntity.ok("좋아요 상태가 변경되었습니다.");
	    }

	    // 특정 리뷰의 좋아요 개수를 반환하는 API
	    @GetMapping("/reviews/{reviewId}/like-count")
	    public ResponseEntity<Long> countLikes(@PathVariable Long reviewId) {
	        Long likeCount = reviewLikeService.countLikes(reviewId);
	        return ResponseEntity.ok(likeCount);
	    }

	    // 사용자가 해당 리뷰에 좋아요를 눌렀는지 여부를 확인하는 API
	    @GetMapping("/reviews/{reviewId}/liked-by/{userId}")
	    public ResponseEntity<Boolean> isLikedByUser(@PathVariable Long reviewId, @PathVariable Long userId) {
	        boolean isLiked = reviewLikeService.isLikedByUser(reviewId, userId);
	        return ResponseEntity.ok(isLiked);
	    }
	}