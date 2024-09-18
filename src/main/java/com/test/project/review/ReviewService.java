package com.test.project.review;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.DataNotFoundException;
import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.img.ReviewImageRepository;
import com.test.project.review.tag.ReviewTag;
import com.test.project.review.tag.ReviewTagMap;
import com.test.project.review.tag.ReviewTagRepository;

@Service
public class ReviewService {

	private final String uploadDirectory = "src/main/resources/static/img/upload";
	
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewTagRepository reviewTagRepository;
    

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, 
                         ReviewImageRepository reviewImageRepository,
                         ReviewTagRepository reviewTagRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.reviewTagRepository = reviewTagRepository;
    }

    // ID로 리뷰 조회
    public Optional<Review> findReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    // ID로 특정 리뷰 조회, 없으면 예외
    public Review getReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Review not found"));
    }

    // 모든 리뷰 조회
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
    
    @Transactional
    public void processTags(List<String> tags, Review review) {
        Set<ReviewTagMap> reviewTagMaps = new HashSet<>();
        for (int i = 0; i < tags.size(); i++) {
            String tagName = tags.get(i);
            ReviewTag tag = reviewTagRepository.findByName(tagName);
            if (tag == null) {
                tag = new ReviewTag();
                tag.setName(tagName);
                reviewTagRepository.save(tag);
            }

            ReviewTagMap tagMap = new ReviewTagMap();
            tagMap.setReviewTag(tag);
            tagMap.setReview(review);
            tagMap.setOrderIndex(i);

            reviewTagMaps.add(tagMap);
        }
        review.setTagMaps(reviewTagMaps);
    }
        
    @Transactional
    public List<ReviewImageMap> processImages(List<MultipartFile> imageFiles, Review review) throws IOException {
        String imagePath = System.getProperty("user.dir") + "/src/main/resources/static/img/upload/";
        List<ReviewImageMap> reviewImageMaps = new ArrayList<>();

        // 이미지 개수 로그 출력
        System.out.println("이미지 개수: " + imageFiles.size());

        for (MultipartFile file : imageFiles) {
            String uuid = UUID.randomUUID().toString();
            String filename = uuid + "_" + file.getOriginalFilename();
            File dest = new File(imagePath + filename);

            // 중복 체크
            if (reviewImageRepository.existsByFilename(filename)) {
                continue; // 중복된 파일은 건너뜁니다.
            }

            // 파일 저장
            file.transferTo(dest);

            // 이미지 객체 생성 및 저장
            ReviewImage image = new ReviewImage();
            image.setFilename(filename);
            image.setFilepath("/img/upload/" + filename);
            reviewImageRepository.save(image);

            // 이미지와 리뷰의 매핑
            ReviewImageMap imageMap = new ReviewImageMap();
            imageMap.setReview(review);
            imageMap.setReviewImage(image);

            reviewImageMaps.add(imageMap);

            System.out.println("Image saved with ID: " + image.getId());
        }

        // 리뷰에 이미지 매핑 설정
        review.setReviewImageMap(reviewImageMaps);
        return reviewImageMaps;
    }
}