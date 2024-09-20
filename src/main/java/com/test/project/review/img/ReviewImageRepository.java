package com.test.project.review.img;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.project.review.tag.ReviewTag;

@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
	boolean existsByFilename(String filename);
    // 필요한 추가 쿼리 메서드를 정의할 수 있습니다.
}
