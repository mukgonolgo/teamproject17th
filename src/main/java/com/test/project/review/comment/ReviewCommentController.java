package com.test.project.review.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.test.project.review.Review;
import com.test.project.review.ReviewService;
import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class ReviewCommentController {

    @Autowired
    private ReviewCommentService reviewCommentService;

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;

    
    //댓글작성
    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam("content") String content) {

        // 현재 로그인한 사용자의 ID 가져오기
        Long userId = reviewService.getCurrentUserId(); // 현재 로그인한 사용자의 ID 가져오기

        // 사용자 정보 가져오기
        SiteUser user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Review review = reviewService.findById(reviewId);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 댓글 저장
        ReviewComment newComment = reviewCommentService.addComment(content, user, review);

        // 댓글과 사용자 정보를 포함한 응답 생성
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setCommentId(newComment.getCommentId());
        commentResponse.setContent(newComment.getContent());
        commentResponse.setCreateDate(newComment.getCreateDate());
        commentResponse.setUserId(user.getId());
        commentResponse.setUsername(user.getUsername());
        commentResponse.setUserImage(user.getImageUrl());  // 프로필 이미지 설정

        return ResponseEntity.ok(commentResponse);
    }


    
    @PostMapping("/reviews/{reviewId}/comments/{parentId}/reply")
    public ResponseEntity<ReviewComment> createReply(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("parentId") Long parentId,
            @RequestParam("content") String content) {

        // 현재 로그인한 사용자의 ID 가져오기
        Long userId = reviewService.getCurrentUserId();

        // 사용자 정보 가져오기
        SiteUser user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Review review = reviewService.findById(reviewId);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 대댓글 추가
        ReviewComment newReply = reviewCommentService.addReply(parentId, content, user);
        return ResponseEntity.ok(newReply);
    }




    // 특정 리뷰에 대한 댓글 목록을 조회하는 엔드포인트
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForReview(@PathVariable Long reviewId) {
        List<CommentResponse> comments = reviewCommentService.getCommentsForReview(reviewId);
        return ResponseEntity.ok(comments);  // 로그인 여부와 상관없이 댓글 반환
    }

}
