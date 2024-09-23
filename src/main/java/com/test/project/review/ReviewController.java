package com.test.project.review;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.review.comment.CommentResponse;
import com.test.project.review.comment.ReviewCommentService;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.like.LikeStatusDto;
import com.test.project.review.like.ReviewLikeService;
import com.test.project.review.tag.ReviewTagMap;
import com.test.project.user.SiteUser;
import com.test.project.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class ReviewController {

    private final String uploadDirectory = "src/main/resources/static/img/upload";

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewCommentService reviewcommentService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikeService reviewLikeService;

    @Autowired
    private UserService userService;

    // 리뷰 목록 페이지를 반환하는 메서드
    @GetMapping("/review")
    public String reviewPage(Model model) {
        List<Review> reviewPage = reviewService.getAllReviews();  // 모든 리뷰 가져오기
        Long userId = reviewService.getCurrentUserId();  // 현재 로그인한 사용자의 ID

        Map<Long, LikeStatusDto> likeStatusMap = new HashMap<>();
        for (Review review : reviewPage) {
            Long reviewId = review.getId();
            boolean likedByUser = false;
            Long likeCount = reviewLikeService.countLikes(reviewId);  // 좋아요 수

            if (userId != null) {
                likedByUser = reviewLikeService.isLikedByUser(reviewId, userId);  // 로그인된 경우 좋아요 여부 확인
            }

            LikeStatusDto likeStatusDto = new LikeStatusDto(likedByUser, likeCount.intValue());
            likeStatusMap.put(reviewId, likeStatusDto);
        }

        model.addAttribute("reviewPage", reviewPage);  // 모든 리뷰 리스트
        model.addAttribute("likeStatusMap", likeStatusMap);  // 각 리뷰의 좋아요 정보

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

        model.addAttribute("review", review);
        model.addAttribute("likedByUser", likedByUser);
        model.addAttribute("likeCount", likeCount);
        model.addAttribute("authorProfileImage", author.getImageUrl());
        model.addAttribute("authorUsername", author.getUsername());
        model.addAttribute("comments", comments);

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
        List<ReviewImageMap> reviewImageMaps = reviewService.processImages(fileUpload, review);  // 이미지 처리

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

        reviewLikeService.toggleLike(id, userId);

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
        Long userId = review.getUser().getId(); // 사용자 ID
        model.addAttribute("review", review); // 리뷰 정보
        model.addAttribute("userId", userId); // 사용자 ID 전달
        return "review/review_update_form";  // 수정 폼 페이지로 이동
    }


    // 리뷰 수정 메서드
    @PostMapping("/review_update/{id}")
    public String updateReview(
        @PathVariable Long id,
        @ModelAttribute Review review,
        @RequestParam(name = "fileUpload", required = false) List<MultipartFile> fileUpload,
        @RequestParam("tagsJson") String tagsJson,
        @RequestParam("rating") String rating,
        Model model) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> tags = objectMapper.readValue(tagsJson, List.class);

            reviewService.updateReview(id, review, fileUpload, tags, rating);

        } catch (IOException e) {
            model.addAttribute("errorMessage", "태그 데이터를 처리하는 중 오류가 발생했습니다.");
            return "review/review_update_form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "리뷰를 업데이트하는 중 오류가 발생했습니다.");
            return "review/review_update_form";
        }

        return "redirect:/review_detail/" + id;
    }

    
    
    
    
}
