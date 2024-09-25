package com.test.project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@SpringBootTest
public class MukgonolgoApplicationTests {
    

    @Test
    public void testProcessImages() throws IOException {
//        // 리뷰 객체 생성 및 초기화
//        Review review = new Review();
//        review.setTitle("Test Review");
//        review.setContent("This is a test review.");
//        review.setCreateDate(LocalDateTime.now());
//
//        // 여러 개의 Mock 이미지 파일 생성
//        MultipartFile file1 = new MockMultipartFile("file1", "test1.jpg", "image/jpeg", "test image 1".getBytes());
//        MultipartFile file2 = new MockMultipartFile("file2", "test2.jpg", "image/jpeg", "test image 2".getBytes());
//        List<MultipartFile> files = List.of(file1, file2);
//
//        // 이미지 처리 및 리뷰 저장
//        Review processedReview = reviewService.processImages(files, review);
//        reviewRepository.save(processedReview);  // 리뷰를 DB에 저장
//
//        // 리뷰에서 이미지 리스트 가져오기
//        List<ReviewImage> images = processedReview.getReviewImage();
//
//        // 이미지의 수와 파일명을 검증
//        assertEquals(2, images.size(), "이미지 개수가 올바르지 않습니다.");
//
//        // 파일명 검증 시 UUID를 제거한 파일명만 비교
//        String expectedFileName1 = "test1.jpg";
//        String expectedFileName2 = "test2.jpg";
//        assertEquals(expectedFileName1, getFileNameWithoutUUID(images.get(0).getFilename()), "첫 번째 이미지 파일명이 올바르지 않습니다.");
//        assertEquals(expectedFileName2, getFileNameWithoutUUID(images.get(1).getFilename()), "두 번째 이미지 파일명이 올바르지 않습니다.");
//
//        // 디버깅 출력
//        images.forEach(img -> 
//            System.out.println("Image Filename: " + img.getFilename())
//        );
//    }
//
//    // UUID를 제거하고 파일명만 반환하는 헬퍼 메서드
//    private String getFileNameWithoutUUID(String filename) {
//        int underscoreIndex = filename.indexOf('_');
//        return underscoreIndex == -1 ? filename : filename.substring(underscoreIndex + 1);
    }
}
