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

    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewComment> createComment(
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

        ReviewComment newComment = reviewCommentService.addComment(content, user, review);
        return ResponseEntity.ok(newComment);
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





    // 리뷰에 속한 댓글 목록 조회
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<List<ReviewComment>> getComments(@PathVariable("reviewId") Long reviewId) {
        // 리뷰 조회
        Review review = reviewService.findById(reviewId);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 해당 리뷰에 속한 댓글 목록 조회
        List<ReviewComment> comments = reviewCommentService.getCommentsByReview(review);
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        reviewCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
    
 

}
