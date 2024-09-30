package com.test.project.review.img;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service

public class ImageService {
	 private final Map<Long, List<ReviewImage>> imageMap = new HashMap<>();  // 선언과 동시에 초기화

	    public ReviewImage getFirstImageByReviewId(Long reviewId) {
	        List<ReviewImage> images = imageMap.get(reviewId);
	        if (images != null && !images.isEmpty()) {
	            return images.get(0);  // 첫 번째 이미지 반환
	        }
	        return null;  // 이미지가 없으면 null 반환
	    }
}