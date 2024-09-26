package com.test.project.review.like;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.project.review.Review;
import com.test.project.review.ReviewDto;
import com.test.project.review.ReviewRepository;
import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
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
    
    
    private final Map<Long, List<ReviewImage>> imageMap = new HashMap<>(); ;  // 리뷰 ID에 매핑된 이미지 리스트
    
  

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

    
    public List<ReviewImageMap> getFirstImagesForLikedReviews(Long userId) {
        // 유저가 좋아요한 리뷰 ID 리스트를 가져옴
        List<Long> likedReviewIds = likeRepository.findLikedReviewIdsByUserId(userId);

        // 각 리뷰에서 첫 번째 이미지를 가져옴
        List<ReviewImageMap> firstImages = new ArrayList<>();
        for (Long reviewId : likedReviewIds) {
            // 리뷰를 찾음
            Optional<Review> review = reviewRepository.findById(reviewId);
            if (review.isPresent()) {
                List<ReviewImageMap> imageMaps = review.get().getReviewImageMap();
                if (!imageMaps.isEmpty()) {
                    // 첫 번째 이미지 매핑 객체를 가져옴
                    firstImages.add(imageMaps.get(0));
                }
            }
        }

        return firstImages;
    }


    
    // 리뷰에 이미지를 추가하는 메서드 (리뷰 등록 시 사용)
    public void addImagesForReview(Long reviewId, List<ReviewImage> images) {
        imageMap.put(reviewId, images);
    }
}