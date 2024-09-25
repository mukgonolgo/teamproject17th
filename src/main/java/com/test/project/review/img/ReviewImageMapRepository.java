package com.test.project.review.img;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewImageMapRepository extends JpaRepository<ReviewImageMap, Long> {
    // ReviewImage 객체를 통해 ReviewImageMap을 찾는 메서드
    List<ReviewImageMap> findByReviewImage(ReviewImage reviewImage);
}
