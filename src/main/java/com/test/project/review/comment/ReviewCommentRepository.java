package com.test.project.review.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.test.project.review.Review;

import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
	
	// 특정 리뷰 ID(reviewId)에 해당하는 모든 댓글을 가져오는 메서드
    List<ReviewComment> findByReviewId(Long reviewId);
    
    // 특정 리뷰에 속한 댓글의 개수를 반환하는 메소드
    long countByReview(Review review);
	  // 부모 댓글을 기준으로 대댓글의 개수를 반환
    long countByParent(ReviewComment parent);
    // 특정 리뷰에 속한 부모 댓글(대댓글이 아닌 댓글)을 조회
    List<ReviewComment> findByReviewIdAndParentIsNull(Long reviewId);
    // 리뷰를 기준으로 댓글 목록을 그룹과 순서대로 가져오는 메소드
    List<ReviewComment> findByReviewOrderByGroupsAscOrdersAsc(Review review);

}
