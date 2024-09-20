package com.test.project.review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.img.ReviewImageRepository;
import com.test.project.review.like.LikeStatusDto;
import com.test.project.review.like.ReviewLikeService;
import com.test.project.review.tag.ReviewTag;
import com.test.project.review.tag.ReviewTagMap;
import com.test.project.review.tag.ReviewTagRepository;
import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import jakarta.transaction.Transactional;

@Controller
public class ReviewController {

    private final String uploadDirectory = "src/main/resources/static/img/upload";

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikeService reviewLikeService;
    

    
    @Autowired
    private UserService userService;
    
    @GetMapping("/review")
    public String reviewPage(Model model) {
        List<Review> reviewPage = reviewService.getAllReviews();  // 리뷰 목록 가져오기
        Long userId = reviewService.getCurrentUserId();  // 현재 로그인한 사용자의 ID
        
        // Map을 사용하여 리뷰 ID와 LikeStatusDto를 연결
        Map<Long, LikeStatusDto> likeStatusMap = new HashMap<>();

        for (Review review : reviewPage) {
            Long reviewId = review.getId();  // 각 리뷰의 ID 가져오기

            // 각 리뷰에 대해 좋아요 상태와 좋아요 수를 가져옴
            boolean likedByUser = reviewLikeService.isLikedByUser(reviewId, userId);
            Long likeCount = reviewLikeService.countLikes(reviewId);

            // LikeStatusDto에 좋아요 상태와 좋아요 수 저장
            LikeStatusDto likeStatusDto = new LikeStatusDto(likedByUser, likeCount.intValue());

            // 각 리뷰 ID별로 LikeStatusDto를 Map에 저장
            likeStatusMap.put(reviewId, likeStatusDto);
        }

        // 모델에 리뷰 페이지와 좋아요 상태 맵 추가
        model.addAttribute("reviewPage", reviewPage);  // 리뷰 리스트
        model.addAttribute("likeStatusMap", likeStatusMap);  // 리뷰별 좋아요 정보

        return "review/review_page";  // 리뷰 페이지로 이동
    }




    @GetMapping("/reviews")
    public String getReviews(@PathVariable("id") Long id,Model model) {
        List<Review> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        
        Long userId = reviewService.getCurrentUserId();  // 현재 로그인한 사용자의 ID
        boolean likedByUser = reviewLikeService.isLikedByUser(id, userId);  // 사용자가 좋아요를 눌렀는지 여부 확인
        Long likeCount = reviewLikeService.countLikes(id);  // 리뷰의 좋아요 수
        
        model.addAttribute("likedByUser", likedByUser);  // 좋아요 여부
        model.addAttribute("likeCount", likeCount);  // 좋아요 수
        
        return "reviews";
    }


    @GetMapping("/review_detail/{id}")
    public String reviewDetail(@PathVariable("id") Long id, Model model) {
        Review review = reviewService.findReviewById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Long userId = reviewService.getCurrentUserId();  // 현재 로그인한 사용자의 ID
        boolean likedByUser = reviewLikeService.isLikedByUser(id, userId);  // 사용자가 좋아요를 눌렀는지 여부 확인
        Long likeCount = reviewLikeService.countLikes(id);  // 리뷰의 좋아요 수

        // 사용자 정보 가져오기
        SiteUser author = userService.getUserById(review.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 모델에 데이터 추가
        model.addAttribute("review", review);
        model.addAttribute("likedByUser", likedByUser);  // 좋아요 여부
        model.addAttribute("likeCount", likeCount);  // 좋아요 수
        model.addAttribute("authorProfileImage", author.getImageUrl());  // 작성자 프로필 이미지
        model.addAttribute("authorUsername", author.getUsername());  // 작성자 사용자명

        return "review/review_detail";
    }




 /*   @GetMapping("/review_feed")
    public String reviewFeed() {
        return "review/review_feed";
    }*/
    
    @GetMapping("/review_feed")
    public String reviewFeed(@RequestParam("user_id") Long userId, Model model) {
        // 사용자 ID로 사용자 정보 조회
        SiteUser user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자의 모든 리뷰 조회
        List<Review> reviews = reviewService.getReviewsByUserId(userId);

        // 모델에 데이터 추가
        model.addAttribute("reviews", reviews);
        model.addAttribute("profileImage", user.getImageUrl()); // 프로필 이미지
        model.addAttribute("username", user.getUsername()); // 사용자 이름
        model.addAttribute("userId", user.getId()); // 사용자 ID 추가

        return "review_feed"; // 반환할 뷰 이름
    }


    @GetMapping("/review_create_form")
    public String reviewCreateForm() {
        return "review/review_create_form";
    }


    @GetMapping("/review/{id}")
    public String getReviewPage(@PathVariable Long id, Model model) {
        Review review = reviewService.getReview(id);
        model.addAttribute("review", review);
        return "review/review_page";
    }

    @GetMapping("/eat")
    public String eat() {
        return "eat_friends";
    }

    @GetMapping("/reservation_completed")
    public String reservationCompleted() {
        return "reservation_completed";
    }

    @GetMapping("/error_404")
    public String error404() {
        return "error_404";
    }

    @GetMapping("/error_500")
    public String error500() {
        return "error_500";
    }

    @GetMapping("/error_403")
    public String error403() {
        return "error_403";
    }

    @GetMapping("/error_401")
    public String error401() {
        return "error_401";
    }

    @GetMapping("/footer")
    public String footer() {
        return "footer";
    }

    @PostMapping("/review_create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReview(
            @ModelAttribute Review review,
            @RequestParam(name = "fileUpload") List<MultipartFile> fileUpload,
            @RequestParam("tags_output") List<String> tags,
            @RequestParam("rating") String rating) throws IOException {

        Long userId = reviewService.getCurrentUserId(); // 현재 사용자 ID 가져오기
     // 사용자 객체 가져오기
        SiteUser user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        // 리뷰 정보 설정
        review.setCreateDate(LocalDateTime.now());
        review.setUser(user); // SiteUser 객체 설정

        // 태그 처리
        reviewService.processTags(tags, review);

        // 이미지 처리
        List<ReviewImageMap> reviewImageMaps = reviewService.processImages(fileUpload, review);

        reviewRepository.save(review);

        // 응답으로 reviewId와 이미지 URL들을 포함한 JSON 반환
        Map<String, Object> response = new HashMap<>();
        response.put("reviewId", review.getId());
        response.put("imageUrls", reviewImageMaps.stream().map(map -> map.getReviewImage().getFilepath()).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    
    
    //좋아요
    @PostMapping("/review/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeReview(@PathVariable Long id) {
        Long userId = reviewService.getCurrentUserId(); // 현재 사용자 ID 가져오기

        // 좋아요 또는 좋아요 취소 처리
        reviewLikeService.toggleLike(id, userId);

        // 리뷰의 현재 좋아요 수
        Long likeCount = reviewLikeService.countLikes(id);

        // 사용자가 해당 리뷰에 좋아요를 눌렀는지 여부
        boolean likedByUser = reviewLikeService.isLikedByUser(id, userId);

        // 응답 데이터 설정
        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeCount);
        response.put("likedByUser", likedByUser);

        return ResponseEntity.ok(response);
    }


}