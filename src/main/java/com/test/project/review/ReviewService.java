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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.DataNotFoundException;
import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.img.ReviewImageRepository;
import com.test.project.review.like.ReviewLikeService;
import com.test.project.review.tag.ReviewTag;
import com.test.project.review.tag.ReviewTagMap;
import com.test.project.review.tag.ReviewTagRepository;
import com.test.project.user.SiteUser;
import com.test.project.user.UserRepository;

@Service
public class ReviewService {

	private final String uploadDirectory = "src/main/resources/static/img/upload";
	
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final ReviewLikeService reviewLikeService;
   
    
    private final UserRepository userRepository ;
    

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, 
                         ReviewImageRepository reviewImageRepository,
                         ReviewTagRepository reviewTagRepository,
                         UserRepository userRepository,
                         ReviewLikeService reviewLikeService) {
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.reviewTagRepository = reviewTagRepository;
        this.userRepository = userRepository;
        this.reviewLikeService = reviewLikeService;
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

    // 모든 리뷰와 사용자 정보 가져오기
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

        
    @Transactional
    public List<ReviewImageMap> processImages(List<MultipartFile> imageFiles, Review review) throws IOException {
        List<ReviewImageMap> reviewImageMaps = new ArrayList<>();
        String imagePath = System.getProperty("user.dir") + "/src/main/resources/static/img/upload/";

        for (MultipartFile file : imageFiles) {
            String uuid = UUID.randomUUID().toString();
            String filename = uuid + "_" + file.getOriginalFilename();
            File dest = new File(imagePath + filename);

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
        }

        review.getReviewImageMap().addAll(reviewImageMaps);
        return reviewImageMaps;
    }


    
    
    @Transactional
    public void processTags(List<String> tags, Review review) {
        // 1. 기존 태그를 불러옴
        Set<ReviewTagMap> existingTagMaps = review.getTagMaps();
        Set<String> existingTags = existingTagMaps.stream()
            .map(tagMap -> tagMap.getReviewTag().getName())
            .collect(Collectors.toSet());

        Set<String> newTags = new HashSet<>(tags);

        // 2. 삭제할 태그를 찾음 (기존 태그 중에서 새로운 태그에 없는 것들)
        Set<ReviewTagMap> tagsToRemove = existingTagMaps.stream()
            .filter(tagMap -> !newTags.contains(tagMap.getReviewTag().getName()))
            .collect(Collectors.toSet());

        // 3. 삭제할 태그들을 리뷰에서 제거
        for (ReviewTagMap tagMap : tagsToRemove) {
            review.getTagMaps().remove(tagMap);  // 리뷰와의 연결을 끊음
            reviewTagRepository.delete(tagMap.getReviewTag());  // 태그 삭제
        }

        // 4. 새로 추가할 태그를 찾음 (새 태그 중에서 기존 태그에 없는 것들)
        Set<String> tagsToAdd = newTags.stream()
            .filter(tag -> !existingTags.contains(tag))
            .collect(Collectors.toSet());

        // 5. 새 태그 추가
        int orderIndex = existingTagMaps.size();
        for (String newTag : tagsToAdd) {
            ReviewTag tag = reviewTagRepository.findByName(newTag);
            if (tag == null) {
                tag = new ReviewTag();
                tag.setName(newTag);
                reviewTagRepository.save(tag);
            }

            ReviewTagMap tagMap = new ReviewTagMap();
            tagMap.setReviewTag(tag);
            tagMap.setReview(review);
            tagMap.setOrderIndex(orderIndex++);
            review.getTagMaps().add(tagMap);
        }

        reviewRepository.save(review);  // 리뷰 저장
    }




    // 현재 로그인한 사용자의 ID를 가져오는 메서드
    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            SiteUser user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("해당 사용자를 찾을 수 없습니다."));
            return user.getId();
        }
        return null;  // 로그인되지 않은 경우 null 반환
    }
    
    // 사용자 ID로 리뷰 조회
    public List<Review> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }
    
 // 리뷰 ID로 사용자 ID를 가져오는 메서드
    public Long getUserIdByReviewId(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getUser() != null ? review.getUser().getId() : null)
                .orElse(null);
    }
    
    

    //리뷰아이디찾기
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + id));
    }
    
    
    
 // 모든 리뷰와 해당 리뷰의 좋아요 수 및 사용자의 좋아요 여부를 반환하는 메서드
    public List<ReviewDto> getAllReviewsWithLikes(Long currentUserId) {
        List<Review> reviews = reviewRepository.findAll();  // 모든 리뷰 가져오기
        List<ReviewDto> reviewDtos = new ArrayList<>();

        // 각 리뷰에 대해 좋아요 정보 추가
        for (Review review : reviews) {
            Long likeCount = reviewLikeService.countLikes(review.getId());  // 좋아요 수 가져오기
            boolean likedByUser = reviewLikeService.isLikedByUser(review.getId(), currentUserId);  // 사용자가 좋아요 눌렀는지 확인
            ReviewDto reviewDto = new ReviewDto(review);  // Review를 ReviewDto로 변환
            reviewDto.setLikeCount(likeCount);
            reviewDto.setLikedByUser(likedByUser);
            reviewDtos.add(reviewDto);
        }

        return reviewDtos;
    }
    
    //리뷰삭제
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        reviewRepository.delete(review);
    }

    public void updateReview(Long id, Review updatedReview, List<MultipartFile> fileUpload, List<String> tags, String rating) throws IOException {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + id));

        // 리뷰 정보 업데이트
        review.setTitle(updatedReview.getTitle());
        review.setContent(updatedReview.getContent());
        review.setRating(rating);

        // 태그 처리
        processTags(tags, review);

        // 이미지 처리 (새로 업로드된 이미지만 추가)
        if (fileUpload != null && !fileUpload.isEmpty()) {
            processImages(fileUpload, review);
        }

        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

   
    }




