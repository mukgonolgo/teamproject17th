package com.test.project.review.img;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
	boolean existsByFilename(String filename);
    // 필요한 추가 쿼리 메서드를 정의할 수 있습니다.
	  // 파일 경로를 통해 이미지를 찾는 메서드
    ReviewImage findByFilepath(String filepath);


}
