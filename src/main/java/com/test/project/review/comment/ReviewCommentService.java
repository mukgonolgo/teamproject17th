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

@Service
public class ReviewCommentService {

    @Autowired
    private ReviewCommentRepository reviewCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;
    
    
    @Transactional
    // 댓글을 추가하는 메소드
    public ReviewComment addComment(String content, SiteUser user, Review review) {
        ReviewComment comment = new ReviewComment();
        comment.setContent(content);  // 댓글 내용 설정
        comment.setUser(user);        // 작성자 설정
        comment.setReview(review);    // 해당 리뷰와 연관
        comment.setReply(false);    // 댓글이므로 isReply를 false로 설정
        comment.setGroups((int)reviewCommentRepository.countByReview(review) + 1); // 새로운 그룹 생성
        return reviewCommentRepository.save(comment); // DB에 댓글 저장
    }

    // 대댓글 추가 (이미 구현한 메소드)
    public ReviewComment addReply(Long parentId, String content, SiteUser user) {
        ReviewComment parentComment = reviewCommentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        
        ReviewComment reply = new ReviewComment();
        reply.setContent(content);
        reply.setUser(user);
        reply.setReview(parentComment.getReview());
        reply.setParent(parentComment);
        reply.setReply(true); // 대댓글이므로 true
        reply.setGroups(parentComment.getGroups()); // 부모 댓글의 그룹을 따른다.
        reply.setOrders((int) reviewCommentRepository.countByParent(parentComment) + 1);
 // 해당 그룹 내의 순서
        return reviewCommentRepository.save(reply);
    }

    // 댓글 조회
    public List<ReviewComment> getCommentsByReview(Review review) {
        return reviewCommentRepository.findByReviewOrderByGroupsAscOrdersAsc(review);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId) {
        reviewCommentRepository.deleteById(commentId);
    }

    // 특정 리뷰에 대한 댓글 목록과 유저 정보를 가져오는 메서드
    public List<CommentResponse> getCommentsForReview(Long reviewId) {
        // 리뷰에 해당하는 모든 댓글을 조회
        List<ReviewComment> comments = reviewCommentRepository.findByReviewId(reviewId);

        // 각 댓글을 CommentResponse로 변환
        return comments.stream().map(comment -> {
            CommentResponse response = new CommentResponse();
            response.setCommentId(comment.getCommentId());
            response.setContent(comment.getContent());
            response.setCreateDate(comment.getCreateDate());

            // 유저 정보 설정
            SiteUser user = comment.getUser();  // 댓글 작성자 정보 가져오기
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setUserImage(user.getImageUrl());

            return response;
        }).collect(Collectors.toList());
    }
   
    
}