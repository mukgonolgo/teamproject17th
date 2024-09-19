package com.test.project.review.like;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.project.review.Review;
import com.test.project.review.ReviewRepository;
import com.test.project.user.SiteUser;
import com.test.project.user.UserRepository;

@Service
public class ReviewLikeService {

    @Autowired
    private ReviewLikeRepository likeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    // 사용자가 리뷰에 좋아요를 누르거나 취소하는 기능
    public void likeReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        SiteUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<ReviewLike> existingLike = likeRepository.findByUserAndReview(user, review);  // 인스턴스 기반 호출
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
        } else {
            ReviewLike like = new ReviewLike();  // Like -> ReviewLike로 수정
            like.setReview(review);
            like.setUser(user);
            likeRepository.save(like);
        }
    }

    // 특정 리뷰의 좋아요 개수를 반환하는 기능
    public Long countLikes(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        return likeRepository.countByReview(review);  // 인스턴스 기반 호출
    }

    // 사용자가 해당 리뷰에 좋아요를 눌렀는지 여부를 확인하는 기능
    public boolean isLikedByUser(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        SiteUser user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return likeRepository.findByUserAndReview(user, review).isPresent();  // 인스턴스 기반 호출
    }
}
