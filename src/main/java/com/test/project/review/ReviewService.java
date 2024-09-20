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
    
    // 현재 로그인한 사용자의 ID를 가져오는 메서드
    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            SiteUser user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new DataNotFoundException("해당 사용자를 찾을 수 없습니다."));
            return user.getId();
        }
        throw new IllegalStateException("로그인된 사용자가 없습니다.");
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
}
