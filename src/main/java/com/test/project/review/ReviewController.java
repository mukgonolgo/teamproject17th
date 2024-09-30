package com.test.project.review;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.DataNotFoundException;
import com.test.project.review.comment.CommentResponse;
import com.test.project.review.comment.ReviewCommentService;
import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.img.ReviewImageMapRepository;
import com.test.project.review.img.ReviewImageRepository;
import com.test.project.review.like.LikeStatusDto;
import com.test.project.review.like.ReviewLikeService;
import com.test.project.review.tag.ReviewTagMap;

import com.test.project.user.SiteUser;
import com.test.project.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.test.project.review.tag.ReviewTagRepository;
import com.test.project.store.Store;
import com.test.project.store.StoreService;
import com.test.project.user.UserService;

import jakarta.transaction.Transactional;


@Controller
public class ReviewController {
   
   @Autowired
   private StoreService storeService;


    private final String uploadDirectory = "src/main/resources/static/img/upload";

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private ReviewImageRepository reviewImageRepository;
    
    @Autowired
    private ReviewImageMapRepository reviewImageMapRepository;


    @Autowired
    private ReviewCommentService reviewcommentService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikeService reviewLikeService;

    @Autowired
    private UserService userService;

    // 리뷰 목록 페이지를 반환하는 메서드
 // 리뷰 목록 페이지를 반환하는 메서드 (검색 기능 추가)
    @GetMapping("/review")
    public String reviewPage(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Review> reviewPage;

        if (query != null && !query.isEmpty()) {
            // 검색어가 있을 경우, 해당 검색어로 리뷰를 검색
            reviewPage = reviewService.searchReviews(query);
        } else {
            // 검색어가 없을 경우, 모든 리뷰를 가져오기
            reviewPage = reviewService.getAllReviews();
        }

        Long userId = reviewService.getCurrentUserId();  // 현재 로그인한 사용자 ID
        List<Store> stores = storeService.getAllStore();  // 모든 가게 가져오기
        model.addAttribute("stores", stores);  // 가게 리스트 추가

        Map<Long, LikeStatusDto> likeStatusMap = new HashMap<>();
        Map<Long, Long> commentCountMap = new HashMap<>();  // 리뷰별 댓글 수 저장할 Map

        for (Review review : reviewPage) {
            Long reviewId = review.getId();
            boolean likedByUser = false;
            Long likeCount = reviewLikeService.countLikes(reviewId);  // 좋아요 수 가져오기

            if (userId != null) {
                likedByUser = reviewLikeService.isLikedByUser(reviewId, userId);  // 사용자가 좋아요를 눌렀는지 확인
            }

            LikeStatusDto likeStatusDto = new LikeStatusDto(likedByUser, likeCount.intValue());
            likeStatusMap.put(reviewId, likeStatusDto);

            // 댓글 수 계산
            long commentCount = reviewcommentService.countCommentsByReviewId(reviewId);
            commentCountMap.put(reviewId, commentCount);  // 각 리뷰별 댓글 수 저장
        }

        // 모델에 데이터 추가
        model.addAttribute("reviewPage", reviewPage);  // 필터링된 리뷰 목록 추가
        model.addAttribute("likeStatusMap", likeStatusMap);  // 좋아요 상태 정보 추가
        model.addAttribute("commentCountMap", commentCountMap);  // 댓글 수 정보 추가

        return "review/review_page";  // 리뷰 페이지로 이동
    }




 // 특정 리뷰에 대한 상세 정보를 반환하는 메서드
    @GetMapping("/review_detail/{id}")
    public String reviewDetail(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Review review = reviewService.findReviewById(id).orElseThrow(() -> new RuntimeException("Review not found"));

        Long userId = null;
        if (userDetails != null) {
            // 로그인한 사용자의 정보를 가져옴
            SiteUser user = userService.getUserByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
            userId = user.getId();
            model.addAttribute("loggedInUserId", userId);  // 로그인한 사용자의 ID를 모델에 추가
        }

        boolean likedByUser = false;
        if (userId != null) {
            likedByUser = reviewLikeService.isLikedByUser(id, userId);  // 로그인한 경우 좋아요 상태 확인
        }
        Long likeCount = reviewLikeService.countLikes(id);  // 리뷰의 좋아요 수

        SiteUser author = userService.getUserById(review.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));

        List<CommentResponse> comments = reviewcommentService.getCommentsForReview(id);  // 댓글 조회
        long commentCount = reviewcommentService.countCommentsByReviewId(id);  // 댓글 수 계산

        model.addAttribute("review", review);
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("authorProfileImage", author.getImageUrl());
        model.addAttribute("authorUsername", author.getUsername());
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", commentCount);  // 댓글 수 모델에 추가

        return "review/review_detail";  // 리뷰 상세 페이지로 이동
    }


    // 리뷰 작성 폼 페이지로 이동하는 메서드
    @GetMapping("/review_create_form")
    public String reviewCreateForm() {
        return "review/review_create_form";  // 리뷰 작성 폼 페이지로 이동
    }

 // 리뷰를 생성하는 메서드 (이미지 및 태그 처리 포함)
    @PostMapping("/review_create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReview(
            @ModelAttribute Review review,
            @RequestParam(name = "fileUpload") List<MultipartFile> fileUpload,
            @RequestParam("tags_output") List<String> tags,
            @RequestParam("rating") String rating) throws IOException {

        Long userId = reviewService.getCurrentUserId();
        SiteUser user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        review.setCreateDate(LocalDateTime.now());
        review.setUser(user);

        reviewService.processTags(tags, review);
        // processImages 메서드 호출 시 기존 이미지 경로는 null 또는 빈 문자열로 전달
        List<ReviewImageMap> reviewImageMaps = reviewService.processImages(fileUpload, review, "");  // 이미지 처리

        reviewRepository.save(review);

        Map<String, Object> response = new HashMap<>();
        response.put("reviewId", review.getId());
        response.put("imageUrls", reviewImageMaps.stream().map(map -> map.getReviewImage().getFilepath()).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }


   

    // 좋아요 기능 처리
    @PostMapping("/review/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeReview(@PathVariable Long id) {
        Long userId = reviewService.getCurrentUserId();

        reviewLikeService.toggleLike(id
        		
        		, userId);

        Long likeCount = reviewLikeService.countLikes(id);
        boolean likedByUser = reviewLikeService.isLikedByUser(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeCount);
        response.put("likedByUser", likedByUser);

        return ResponseEntity.ok(response);
    }

    // 리뷰 삭제 메서드
    @DeleteMapping("/api/review/{id}")
    public ResponseEntity<String> deleteReview(
        @PathVariable Long id, 
        @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String currentUsername = userDetails.getUsername();
        SiteUser currentUser = userService.getUserByUsername(currentUsername).orElseThrow(() -> new RuntimeException("User not found"));

        Review review = reviewService.getReview(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }

    

        reviewService.deleteReview(id);
        return ResponseEntity.ok("Review deleted successfully");
    }

    // 별점 데이터 가져오기
    @GetMapping("/api/review/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReviewDetails(@PathVariable("id") Long id) {
        Review review = reviewService.getReview(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("rating", review.getRating());
        response.put("title", review.getTitle());
        response.put("content", review.getContent());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/review_update_form/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Review review = reviewService.findReviewById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // 태그 리스트 가져오기
        List<String> tags = review.getTagMaps().stream()
                .map(tagMap -> tagMap.getReviewTag().getName())
                .collect(Collectors.toList());

     // 이미지 ID와 경로를 담기 위한 리스트
        List<Map<String, String>> imageData = review.getReviewImageMap().stream()
            .map(imageMap -> {
                Map<String, String> imgData = new HashMap<>();
                imgData.put("id", String.valueOf(imageMap.getReviewImage().getId())); // 이미지 ID
                imgData.put("filepath", imageMap.getReviewImage().getFilepath()); // 이미지 경로
                return imgData;
            })
            .collect(Collectors.toList());

        model.addAttribute("review", review);
        model.addAttribute("tags", String.join(",", tags)); // 태그를 쉼표로 구분된 문자열로 전달
        model.addAttribute("imageData", imageData); // 이미지 ID와 경로 리스트 전달

        return "review/review_update_form"; // 수정 페이지로 이동
    }


    // 리뷰 수정 메서드
    @PostMapping("/review_update/{id}")
    @ResponseBody  // JSON 형식으로 응답
    public ResponseEntity<Map<String, Object>> updateReview(
            @PathVariable Long id,
            @ModelAttribute Review review,
            @RequestParam(name = "fileUpload", required = false) List<MultipartFile> fileUpload,
            @RequestParam(name = "existingImages", required = false) String existingImages, // 기존 이미지 경로
            @RequestParam("tags") String tags,
            @RequestParam("rating") String rating) {

        Map<String, Object> response = new HashMap<>();
        
        // 태그 데이터를 쉼표로 분리하여 리스트로 변환
        List<String> tagsList = Arrays.asList(tags.split(","));

        try {
            // 리뷰 업데이트 처리
            reviewService.updateReview(id, review, fileUpload, existingImages, tagsList, rating);

            // 성공 응답
            response.put("status", "success");
            response.put("message", "리뷰가 성공적으로 업데이트되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 오류 발생 시 에러 로그 출력
            e.printStackTrace();  // 예외 출력
            System.err.println("오류 발생: " + e.getMessage());  // 오류 메시지 출력

            // 에러 발생 시 에러 메시지 반환
            response.put("status", "error");
            response.put("message", "리뷰를 업데이트하는 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




 // 서버에서 태그를 직렬화할 때:
    @GetMapping("/get_tags/{reviewId}")
    @ResponseBody
    public List<String> getTags(@PathVariable Long reviewId) {
        Review review = reviewService.findById(reviewId);
        return review.getTagMaps().stream()
                     .map(tagMap -> tagMap.getReviewTag().getName()) // 태그 이름만 추출
                     .collect(Collectors.toList());
    }
    

    
    @DeleteMapping("/delete_image_by_url")
    @ResponseBody
    public ResponseEntity<String> deleteImageByUrl(@RequestBody Map<String, String> request) {
        String imageUrl = request.get("imageUrl");

        // 이미지 URL로부터 ReviewImage 찾기
        ReviewImage image = reviewImageRepository.findByFilepath(imageUrl)
                .orElseThrow(() -> new DataNotFoundException("Image not found"));

        // ReviewImage 객체로부터 ReviewImageMap 찾기
        List<ReviewImageMap> imageMaps = reviewImageMapRepository.findByReviewImage(image);

        if (imageMaps.isEmpty()) {
            throw new DataNotFoundException("Image map not found");
        }

        // 이미지와 매핑 데이터 삭제
        for (ReviewImageMap imageMap : imageMaps) {
            ReviewImage img = imageMap.getReviewImage();

            // 실제 파일 삭제
            File fileToDelete = new File(System.getProperty("user.dir") + "/src/main/resources/static" + img.getFilepath());
            if (fileToDelete.exists() && fileToDelete.delete()) {
                System.out.println("파일이 성공적으로 삭제되었습니다: " + img.getFilepath());
            } else {
                System.out.println("파일 삭제 실패 또는 파일이 존재하지 않음: " + img.getFilepath());
            }

            // 데이터베이스에서 매핑과 이미지 삭제
            reviewImageMapRepository.delete(imageMap);  // 매핑 삭제
            reviewImageRepository.delete(img);  // 이미지 삭제
        }

        return ResponseEntity.ok("이미지가 성공적으로 삭제되었습니다.");
    }

    
    @GetMapping("/user_feed/{userId}")
    public String userFeed(@PathVariable Long userId, Model model) {
        SiteUser user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Review> reviews = reviewService.getReviewsByUserId(userId);  // 유저의 리뷰 목록 가져오기
        List<Map<String, Object>> reviewImages = new ArrayList<>();

        for (Review review : reviews) {
            List<ReviewImage> images = review.getImages();  // 리뷰의 이미지 리스트 가져오기
            if (!images.isEmpty()) {
                Map<String, Object> map = new HashMap<>();
                map.put("imagePath", images.get(0).getFilepath());  // 첫 번째 이미지의 파일 경로
                map.put("reviewId", review.getId());              // 리뷰 ID
                reviewImages.add(map);
            }
        }

        model.addAttribute("reviewImages", reviewImages);          // 이미지 및 리뷰 ID 데이터 추가
        model.addAttribute("usernickname", user.getNickname());     // 유저 닉네임 추가
        model.addAttribute("profileImage", user.getImageUrl());    // 프로필 이미지 추가
        return "review/review_feed";                               // 피드 페이지로 이동
    }


}


    