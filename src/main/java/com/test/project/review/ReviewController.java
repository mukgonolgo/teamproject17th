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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.review.comment.CommentResponse;
import com.test.project.review.comment.ReviewComment;
import com.test.project.review.comment.ReviewCommentService;
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
            boolean likedByUser = false;  // 기본값: 좋아요 안 함
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
 // 특정 리뷰에 대한 상세 정보를 반환하는 메서드
    @GetMapping("/review_detail/{id}")
    public String reviewDetail(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Review review = reviewService.findReviewById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Long userId = null;
        if (userDetails != null) {
            // 로그인한 사용자의 정보를 가져옴
            SiteUser user = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            userId = user.getId();
            model.addAttribute("loggedInUserId", userId); // 로그인한 사용자의 ID를 모델에 추가
        }

        // 좋아요 정보 처리
        boolean likedByUser = false;
        if (userId != null) {
            likedByUser = reviewLikeService.isLikedByUser(id, userId);  // 로그인한 경우 좋아요 상태 확인
        }
        Long likeCount = reviewLikeService.countLikes(id);  // 리뷰의 좋아요 수

        // 작성자 정보
        SiteUser author = userService.getUserById(review.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 댓글 목록 조회
        List<CommentResponse> comments = reviewcommentService.getCommentsForReview(id);  // 댓글 조회 서비스 호출

        // 리뷰와 관련된 데이터를 모델에 추가
        model.addAttribute("review", review);
        model.addAttribute("likedByUser", likedByUser);  // 좋아요 여부
        model.addAttribute("likeCount", likeCount);  // 좋아요 수
        model.addAttribute("authorProfileImage", author.getImageUrl());  // 작성자의 프로필 이미지
        model.addAttribute("authorUsername", author.getUsername());  // 작성자의 사용자명
        model.addAttribute("comments", comments);  // 댓글 리스트를 모델에 추가

        return "review/review_detail";  // 리뷰 상세 페이지로 이동
    }


    // 특정 사용자의 리뷰 피드를 반환하는 메서드
    @GetMapping("/review_feed")
    public String reviewFeed(@RequestParam("user_id") Long userId, Model model) {
        SiteUser user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Review> reviews = reviewService.getReviewsByUserId(userId);  // 특정 사용자의 리뷰 조회

        model.addAttribute("reviews", reviews);  // 리뷰 목록
        model.addAttribute("profileImage", user.getImageUrl());  // 사용자 프로필 이미지
        model.addAttribute("username", user.getUsername());  // 사용자 이름
        model.addAttribute("userId", user.getId());  // 사용자 ID

        return "review_feed";  // 리뷰 피드 페이지로 이동
    }

    // 리뷰 작성 폼 페이지로 이동하는 메서드
    @GetMapping("/review_create_form")
    public String reviewCreateForm() {
        return "review/review_create_form";  // 리뷰 작성 폼 페이지로 이동
    }

    // 특정 리뷰 페이지를 반환하는 메서드 (미사용 메서드 같음)
    @GetMapping("/review/{id}")
    public String getReviewPage(@PathVariable Long id, Model model) {
        Review review = reviewService.getReview(id);  // 특정 리뷰 조회
        model.addAttribute("review", review);  // 리뷰 정보를 모델에 추가
        return "review/review_page";
    }

    // 'Eat with friends' 페이지로 이동하는 메서드
    @GetMapping("/eat")
    public String eat() {
        return "eat_friends";  // '친구와 식사하기' 페이지로 이동
    }

    // 예약 완료 페이지로 이동하는 메서드
    @GetMapping("/reservation_completed")
    public String reservationCompleted() {
        return "reservation_completed";  // 예약 완료 페이지로 이동
    }

    // 각종 에러 페이지로 이동하는 메서드들
    @GetMapping("/error_404")
    public String error404() {
        return "error_404";  // 404 에러 페이지로 이동
    }

    @GetMapping("/error_500")
    public String error500() {
        return "error_500";  // 500 에러 페이지로 이동
    }

    @GetMapping("/error_403")
    public String error403() {
        return "error_403";  // 403 에러 페이지로 이동
    }

    @GetMapping("/error_401")
    public String error401() {
        return "error_401";  // 401 에러 페이지로 이동
    }

    // 푸터 페이지로 이동하는 메서드
    @GetMapping("/footer")
    public String footer() {
        return "footer";  // 푸터 페이지로 이동
    }

    // 리뷰를 생성하는 메서드 (이미지 및 태그 처리 포함)
    @PostMapping("/review_create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReview(
            @ModelAttribute Review review,
            @RequestParam(name = "fileUpload") List<MultipartFile> fileUpload,
            @RequestParam("tags_output") List<String> tags,
            @RequestParam("rating") String rating) throws IOException {

        Long userId = reviewService.getCurrentUserId();  // 현재 사용자 ID 가져오기
        SiteUser user = userService.getUserById(userId)
            .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));
        
        review.setCreateDate(LocalDateTime.now());  // 리뷰 생성 날짜 설정
        review.setUser(user);  // 작성자 설정

        reviewService.processTags(tags, review);  // 태그 처리
        List<ReviewImageMap> reviewImageMaps = reviewService.processImages(fileUpload, review);  // 이미지 처리

        reviewRepository.save(review);  // 리뷰 저장

        Map<String, Object> response = new HashMap<>();
        
        response.put("reviewId", review.getId());
        response.put("imageUrls", reviewImageMaps.stream().map(map -> map.getReviewImage().getFilepath()).collect(Collectors.toList()));

        return ResponseEntity.ok(response);  // JSON 응답 반환
    }

    // 좋아요 기능을 처리하는 메서드
    @PostMapping("/review/{id}/like")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> likeReview(@PathVariable Long id) {
        Long userId = reviewService.getCurrentUserId();  // 현재 사용자 ID 가져오기

        reviewLikeService.toggleLike(id, userId);  // 좋아요 또는 좋아요 취소 처리

        Long likeCount = reviewLikeService.countLikes(id);  // 리뷰의 좋아요 수
        boolean likedByUser = reviewLikeService.isLikedByUser(id, userId);  // 사용자가 좋아요 눌렀는지 확인

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeCount);  // 좋아요 수
        response.put("likedByUser", likedByUser);  // 좋아요 여부

        return ResponseEntity.ok(response);  // JSON 응답 반환
    }

  
    //별점 데이터 가져오기
    @GetMapping("/api/review/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReviewDetails(@PathVariable("id") Long id) {
        Review review = reviewService.getReview(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("rating", review.getRating()); // 별점 정보
        response.put("title", review.getTitle());
        response.put("content", review.getContent());

        return ResponseEntity.ok(response); // JSON으로 응답
    }
   
    @DeleteMapping("/api/review/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        
        String currentUserId = userDetails.getUsername();  // 현재 사용자의 이름 가져오기
        Review review = reviewService.getReview(id);

        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
        }

        if (!review.getUser().getUsername().equals(currentUserId)) {  // 사용자 확인
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to delete this review.");
        }

        reviewService.deleteReview(id);  // 리뷰 삭제 수행
        return ResponseEntity.ok("Review deleted successfully");
    }




}