package com.test.project.review.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;  // 트랜잭션 관리를 위한 import

import com.test.project.DataNotFoundException;
import com.test.project.review.Review;
import com.test.project.review.ReviewRepository;
import com.test.project.review.ReviewService;
import com.test.project.user.SiteUser;
import com.test.project.user.UserRepository;
import com.test.project.user.UserService;

@Service
public class ReviewCommentService {

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    // 댓글 추가
    public ReviewComment addComment(String content, SiteUser user, Review review) {
        ReviewComment comment = new ReviewComment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setReview(review);
        comment.setCreateDate(LocalDateTime.now());
        return reviewCommentRepository.save(comment);
    }

    // 대댓글 추가
    public ReviewComment addReply(Long parentId, String content, SiteUser user) {
        ReviewComment parentComment = reviewCommentRepository.findById(parentId)
            .orElseThrow(() -> new DataNotFoundException("부모 댓글을 찾을 수 없습니다."));
        
        ReviewComment reply = new ReviewComment();
        reply.setContent(content);
        reply.setUser(user);
        reply.setReview(parentComment.getReview());
        reply.setParent(parentComment);  // 부모 댓글 설정
        reply.setCreateDate(LocalDateTime.now());

        return reviewCommentRepository.save(reply);
    }

    // 댓글 수정
    public boolean updateComment(Long commentId, String content, Long userId) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
            .orElseThrow(() -> new DataNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            return false;  // 사용자가 작성한 댓글이 아닌 경우 수정 불가
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        reviewCommentRepository.save(comment);
        return true;
    }

    // 댓글 삭제
    public boolean deleteComment(Long commentId, Long userId) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
            .orElseThrow(() -> new DataNotFoundException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            return false;  // 사용자가 작성한 댓글이 아닌 경우 삭제 불가
        }

        reviewCommentRepository.delete(comment);
        return true;
    }

    // 특정 리뷰에 대한 댓글과 대댓글 조회
    public List<CommentResponse> getCommentsForReview(Long reviewId) {
        List<ReviewComment> comments = reviewCommentRepository.findByReviewIdAndParentIsNull(reviewId);
        return comments.stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }
    
    // 댓글수 계산 메서드
    public long countCommentsByReviewId(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new DataNotFoundException("리뷰를 찾을 수 없습니다."));
        return reviewCommentRepository.countByReview(review);
    }

}
