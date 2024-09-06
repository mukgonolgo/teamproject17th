package com.test.project;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.test.project.review.Review;
import com.test.project.review.ReviewImage;
import com.test.project.review.ReviewRepository;
import com.test.project.review.ReviewImageRepository;

@SpringBootTest
public class MukgonolgoApplicationTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @Test
    void contextLoads() {
        // Create a new Review
        Review review = new Review();
        review.setTitle("맛있는집!");
        review.setContent("추천합니다!");
        review.setUploadDate(LocalDateTime.now());

        // Save the Review
        Review savedReview = reviewRepository.save(review);

        // Create and associate ReviewImages
        ReviewImage image1 = new ReviewImage();
        image1.setImageUrl("img/food/food_2.png");
        image1.setReview(savedReview);

        ReviewImage image2 = new ReviewImage();
        image2.setImageUrl("img/food/food2_1.png");
        image2.setReview(savedReview);

        ReviewImage image3 = new ReviewImage();
        image3.setImageUrl("img/food/food2_2.png");
        image3.setReview(savedReview);

        // 이미지저장
        reviewImageRepository.save(image1);
        reviewImageRepository.save(image2);
        reviewImageRepository.save(image3);

      
    }
}
