package com.test.project.review.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.test.project.DataNotFoundException;
import com.test.project.review.Review;
import com.test.project.review.ReviewService;
import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/comments")
public class ReviewCommentController {

    @Autowired
    private ReviewCommentService reviewCommentService;
    
    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;

    // 댓글 작성
    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam("content") String content) {

        Long userId = reviewService.getCurrentUserId(); // 현재 로그인한 사용자 ID

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

        // 댓글 응답 생성
        CommentResponse commentResponse = new CommentResponse(newComment);

        return ResponseEntity.ok(commentResponse);
    }

    // 대댓글 작성
    @PostMapping("/reviews/{reviewId}/comments/{parentId}/reply")
    public ResponseEntity<CommentResponse> createReply(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("parentId") Long parentId,
            @RequestParam("content") String content) {

        Long userId = reviewService.getCurrentUserId(); // 현재 로그인한 사용자 ID

        SiteUser user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Review review = reviewService.findById(reviewId);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 대댓글 저장
        ReviewComment reply = reviewCommentService.addReply(parentId, content, user);
        CommentResponse replyResponse = new CommentResponse(reply);

        return ResponseEntity.ok(replyResponse);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestParam("content") String content) {

        Long userId = reviewService.getCurrentUserId();
        boolean isUpdated = reviewCommentService.updateComment(commentId, content, userId);

        if (isUpdated) {
            ReviewComment updatedComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("댓글을 찾을 수 없습니다."));

            CommentResponse response = new CommentResponse(updatedComment);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {
        Long userId = reviewService.getCurrentUserId();

        boolean isDeleted = reviewCommentService.deleteComment(commentId, userId);

        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 특정 리뷰에 대한 댓글 목록 조회
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForReview(@PathVariable Long reviewId) {
        List<CommentResponse> comments = reviewCommentService.getCommentsForReview(reviewId);
        return ResponseEntity.ok(comments);
    }
}
