package com.test.project.review.img;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
	boolean existsByFilename(String filename);

	 // 이미지 경로로 ReviewImage를 찾는 메서드 추가
    Optional<ReviewImage> findByFilepath(String filepath);
	}
   
    


