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

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestParam("content") String content) {

        // 현재 로그인한 사용자 확인
        Long userId = reviewService.getCurrentUserId();
        boolean isUpdated = reviewCommentService.updateComment(commentId, content, userId);

        if (isUpdated) {
            // 수정된 댓글 데이터 반환
            ReviewComment updatedComment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("댓글을 찾을 수 없습니다."));
            
            CommentResponse response = new CommentResponse();
            response.setCommentId(updatedComment.getCommentId());
            response.setContent(updatedComment.getContent());
            response.setCreateDate(updatedComment.getCreateDate());
            response.setUpdatedAt(updatedComment.getUpdatedAt());
            response.setUserId(updatedComment.getUser().getId());
            response.setUsername(updatedComment.getUser().getUsername());
            response.setUserImage(updatedComment.getUser().getImageUrl());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 수정 권한이 없는 경우
        }
    }



    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {
        // 현재 로그인한 사용자 확인
        Long userId = reviewService.getCurrentUserId();

        // 댓글 삭제 요청을 서비스에 위임
        boolean isDeleted = reviewCommentService.deleteComment(commentId, userId);

        if (isDeleted) {
            return ResponseEntity.ok().build(); // 성공적으로 삭제된 경우
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 삭제 권한이 없는 경우
        }
    }

    
    // 답글 작성
    @PostMapping("/reviews/{reviewId}/comments/{parentId}/reply")
    public ResponseEntity<CommentResponse> createReply(
            @PathVariable("reviewId") Long reviewId,
            @PathVariable("parentId") Long parentId,
            @RequestParam("content") String content) {

        Long userId = reviewService.getCurrentUserId();
        SiteUser user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Review review = reviewService.findById(reviewId);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 답글 저장
        ReviewComment reply = reviewCommentService.addReply(parentId, content, user);
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setCommentId(reply.getCommentId());
        commentResponse.setContent(reply.getContent());
        commentResponse.setCreateDate(reply.getCreateDate());
        commentResponse.setUserId(user.getId());
        commentResponse.setUsername(user.getUsername());
        commentResponse.setUserImage(user.getImageUrl());

        return ResponseEntity.ok(commentResponse);
    }


    // 특정 리뷰에 대한 댓글 목록을 조회하는 엔드포인트
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<List<CommentResponse>> getCommentsForReview(@PathVariable Long reviewId) {
        List<CommentResponse> comments = reviewCommentService.getCommentsForReview(reviewId);
        return ResponseEntity.ok(comments);  // 로그인 여부와 상관없이 댓글 반환
    }
    


}
