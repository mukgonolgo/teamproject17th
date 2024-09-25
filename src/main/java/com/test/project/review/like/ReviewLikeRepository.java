package com.test.project.review.like;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.project.review.Review;
import com.test.project.user.SiteUser;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
	
	   
	   // 특정 리뷰의 좋아요 개수를 세는 메소드
	    Long countByReview(Review review);

	    // 특정 유저가 특정 리뷰에 좋아요를 눌렀는지 여부를 확인하는 메소드
	    Optional<ReviewLike> findByUserAndReview(SiteUser user, Review review);
}
