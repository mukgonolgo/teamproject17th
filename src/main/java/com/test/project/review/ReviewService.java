package com.test.project.review;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.test.project.review.img.ReviewImageMapRepository;
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
    private final ReviewImageMapRepository reviewImageMapRepository;
    private final ReviewTagRepository reviewTagRepository;
    private final ReviewLikeService reviewLikeService;
   
    
    private final UserRepository userRepository ;
    

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, 
                         ReviewImageRepository reviewImageRepository,
                         ReviewTagRepository reviewTagRepository,
                         UserRepository userRepository,
                         ReviewLikeService reviewLikeService,
                         ReviewImageMapRepository reviewImageMapRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.reviewImageMapRepository = reviewImageMapRepository;
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

  
 // 리뷰 업데이트 처리
    @Transactional
    public void updateReview(Long id, Review updatedReview, List<MultipartFile> fileUpload, 
                             String existingImages, List<String> tags, String rating) throws IOException {

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + id));

        System.out.println("Starting updateReview for review ID: " + id);

        // 리뷰 정보 업데이트
        review.setTitle(updatedReview.getTitle());
        review.setContent(updatedReview.getContent());
        review.setRating(rating);

        System.out.println("Updated review title: " + updatedReview.getTitle());

        // 태그 처리
        processTags(tags, review);
        System.out.println("Processed tags for review ID: " + id);

        // 새로 업로드된 이미지 처리
        if (fileUpload != null && !fileUpload.isEmpty()) {
            processImages(fileUpload, review, existingImages); // 수정된 부분
            System.out.println("Processed new images for review ID: " + id);
        }

        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        System.out.println("Updated review ID: " + id + " successfully.");
    }
    

    
    @Transactional
    public List<ReviewImageMap> processImages(List<MultipartFile> imageFiles, Review review, String existingImages) throws IOException {
        List<ReviewImageMap> reviewImageMaps = new ArrayList<>();
        String imagePath = System.getProperty("user.dir") + "/src/main/resources/static/img/upload/";

        // 서버로 전달된 파일 개수 출력
        System.out.println("서버가 수신한 파일 개수: " + imageFiles.size());
        System.out.println("이미지 저장 경로: " + imagePath);  // 저장 경로 확인

        // 각 파일 이름 출력
        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);
            System.out.println("서버에서 받은 파일 " + (i + 1) + ": " + file.getOriginalFilename());
        }

        for (MultipartFile file : imageFiles) {
            if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
                continue;  // 파일이 비어있거나 잘못된 경우는 건너뜀
            }

            // 중복 파일 처리 방지 로직 개선
            String filenameWithUUID = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File dest = new File(imagePath + filenameWithUUID);
            
            // 파일 저장 로직에 try-catch 추가
            try {
                file.transferTo(dest);
                System.out.println("파일이 성공적으로 저장되었습니다: " + filenameWithUUID);
            } catch (IOException e) {
                System.err.println("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
                e.printStackTrace();
                continue;  // 파일 저장에 실패한 경우 해당 파일 건너뜀
            }

            // 이미지 객체 생성 및 저장
            ReviewImage image = new ReviewImage();
            image.setFilename(filenameWithUUID);
            image.setFilepath("/img/upload/" + filenameWithUUID);
            reviewImageRepository.save(image);  // 새로운 이미지 DB에 저장

            // 이미지와 리뷰의 매핑
            ReviewImageMap imageMap = new ReviewImageMap();
            imageMap.setReview(review);
            imageMap.setReviewImage(image);
            reviewImageMaps.add(imageMap);
        }

        // 기존 이미지 처리: 유지할 이미지 경로만 남기고 나머지 이미지를 삭제
        Set<String> existingImagesSet = new HashSet<>();
        if (existingImages != null && !existingImages.isEmpty()) {
            existingImagesSet = new HashSet<>(Arrays.asList(existingImages.split(",")));
        }

        List<ReviewImageMap> currentImageMaps = review.getReviewImageMap();
        List<ReviewImageMap> imagesToRemove = new ArrayList<>();

        for (ReviewImageMap imageMap : currentImageMaps) {
            String fullPath = imageMap.getReviewImage().getFilepath();

            if (!existingImagesSet.contains(fullPath)) {
                // 파일 삭제 시도
                File fileToDelete = new File(System.getProperty("user.dir") + "/src/main/resources/static" + fullPath);
                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        System.out.println("파일이 성공적으로 삭제되었습니다: " + fullPath);
                    } else {
                        System.out.println("파일 삭제에 실패했습니다: " + fullPath);
                    }
                }

                imagesToRemove.add(imageMap);  // 삭제할 이미지 매핑 추가
            }
        }

        // 기존 이미지에서 삭제할 매핑 제거
        currentImageMaps.removeAll(imagesToRemove);

        // 새로운 이미지를 추가한 후, 해당 이미지를 리뷰에 매핑
        currentImageMaps.addAll(reviewImageMaps);

        // 리뷰 저장
        reviewRepository.save(review);

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


   
    }




