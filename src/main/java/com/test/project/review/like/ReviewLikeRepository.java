package com.test.project.review.like;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.test.project.review.Review;
import com.test.project.user.SiteUser;

import io.lettuce.core.dynamic.annotation.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
	
	   
	   // 특정 리뷰의 좋아요 개수를 세는 메소드
	    Long countByReview(Review review);

	    // 특정 유저가 특정 리뷰에 좋아요를 눌렀는지 여부를 확인하는 메소드
	    Optional<ReviewLike> findByUserAndReview(SiteUser user, Review review);
	    
	    // 특정 유저가 좋아요한 모든 리뷰 목록을 반환하는 메소드
	    List<ReviewLike> findByUser(SiteUser user);
	    
	    List<ReviewLike> findByUserId(Long userId);  // 메서드 이름에 따라 자동으로 쿼리 생성
	    
	 // 좋아요한 리뷰 ID 리스트를 찾는 쿼리 (named parameter 사용)
	 // 네이티브 쿼리로 변경
	    @Query("SELECT r.review.id FROM ReviewLike r WHERE r.user.id = ?1")
	    List<Long> findLikedReviewIdsByUserId(Long userId);


}
