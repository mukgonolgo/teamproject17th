package com.test.project.review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.test.project.DataNotFoundException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Optional<Review> findReviewById(Long id) {
        Optional<Review> reviewOptional = reviewRepository.findById(id);

        if (reviewOptional.isPresent()) {
            Review review = reviewOptional.get();
            // 리뷰 처리 로직
            return Optional.of(review);
        } else {
            // 리뷰가 존재하지 않을 때의 처리 로직
            return Optional.empty();
        }
    }
    

    // 리뷰 저장 메서드
    public Review save(Review review) {
        return reviewRepository.save(review);
    }
    
    // ID로 특정 리뷰를 조회 (getReview 메서드)
    public Review getReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Review not found"));
    }

    public void create(String subject, String content) {
        Review r = new Review();
        r.setSubject(subject);
        r.setContent(content);
        r.setCreateDate(LocalDateTime.now());
        this.reviewRepository.save(r);
    }

    // 모든 리뷰를 조회
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // ID로 특정 리뷰를 조회
    public Review findById(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.orElse(null);
    }

}
