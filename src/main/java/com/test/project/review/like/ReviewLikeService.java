package com.test.project.review.like;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.project.review.Review;
import com.test.project.review.ReviewDto;
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

    // 사용자가 특정 리뷰에 좋아요를 눌렀는지 확인하는 메서드
    public boolean isLikedByUser(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        SiteUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return likeRepository.findByUserAndReview(user, review).isPresent();
    }

    // 리뷰에 대한 좋아요 수를 반환하는 메서드
    public Long countLikes(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return likeRepository.countByReview(review);
    }

    // 사용자가 리뷰에 좋아요를 눌렀거나 취소하는 메서드 (토글 기능)
    public void toggleLike(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        SiteUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<ReviewLike> existingLike = likeRepository.findByUserAndReview(user, review);
        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
        } else {
            // 좋아요 추가
            ReviewLike newLike = new ReviewLike();
            newLike.setReview(review);
            newLike.setUser(user);
            likeRepository.save(newLike);
        }

        // 좋아요 수 및 사용자 상태 업데이트 (Review 엔티티에 업데이트)
        review.setLikeCount((long) review.getLikes().size());
        review.setLikedByUser(review.getLikes().stream()
                .anyMatch(like -> like.getUser().equals(user)));
    }

    // 특정 리뷰의 상세 정보를 가져오는 메서드 (좋아요 정보 포함)
    public ReviewDto getReviewDetails(Long reviewId, SiteUser currentUser) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        // 좋아요 수 및 사용자가 좋아요를 눌렀는지 여부를 업데이트
        review.setLikeCount((long) review.getLikes().size());
        review.setLikedByUser(review.getLikes().stream()
                .anyMatch(like -> like.getUser().equals(currentUser)));

        // 다른 로직들 진행 후 DTO로 변환해서 반환
        return new ReviewDto(review);
    }
}
