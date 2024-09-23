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
    
    
    //댓글 수정
    @Transactional
    public boolean updateComment(Long commentId, String content, Long userId) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
            .orElseThrow(() -> new DataNotFoundException("댓글을 찾을 수 없습니다."));

        // 현재 로그인한 사용자가 댓글 작성자인지 확인
        if (comment.getUser().getId().equals(userId)) {
            comment.setContent(content); // 새로운 내용으로 수정
            comment.setUpdatedAt(LocalDateTime.now()); // 수정 시간 업데이트
            reviewCommentRepository.save(comment); // 수정된 댓글 저장
            return true; // 수정 성공
        } else {
            return false; // 수정 실패
        }
    }


    
    //댓글 삭제
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
            .orElseThrow(() -> new DataNotFoundException("댓글을 찾을 수 없습니다."));

        // 현재 로그인한 사용자가 댓글 작성자인지 확인
        if (comment.getUser().getId().equals(userId)) {
            reviewCommentRepository.delete(comment);
            return true; // 삭제 성공
        } else {
            // 댓글 작성자가 아니면 삭제 권한이 없음을 반환
            return false; // 삭제 실패
        }
    }
    

    // 대댓글 추가 (이미 구현한 메소드)
    @Transactional
    public ReviewComment addReply(Long parentId, String content, SiteUser user) {
        ReviewComment parentComment = reviewCommentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

        ReviewComment reply = new ReviewComment();
        reply.setContent(content);
        reply.setUser(user);
        reply.setReview(parentComment.getReview());
        reply.setParent(parentComment);  // 부모 댓글 설정
        reply.setReply(true);  // 답글이므로 true로 설정
        reply.setGroups(parentComment.getGroups());  // 부모 댓글의 그룹을 따름
        reply.setOrders((int) reviewCommentRepository.countByParent(parentComment) + 1);  // 그룹 내 순서

        return reviewCommentRepository.save(reply);  // 답글 저장
    }

    // 댓글 조회
    public List<ReviewComment> getCommentsByReview(Review review) {
        return reviewCommentRepository.findByReviewOrderByGroupsAscOrdersAsc(review);
    }

   


    // 특정 리뷰에 대한 댓글 목록과 유저 정보를 가져오는 메서드
    public List<CommentResponse> getCommentsForReview(Long reviewId) {
        List<ReviewComment> comments = reviewCommentRepository.findByReviewId(reviewId);

        return comments.stream()
                .map(comment -> {
                    CommentResponse response = new CommentResponse();
                    response.setCommentId(comment.getCommentId());
                    response.setUpdatedAt(comment.getUpdatedAt());
                    response.setContent(comment.getContent());
                    response.setCreateDate(comment.getCreateDate());
                    response.setUserId(comment.getUser().getId());
                    response.setUsername(comment.getUser().getUsername());
                    response.setUserImage(comment.getUser().getImageUrl());

                    // 답글이 있으면 답글 목록도 추가
                    if (!comment.getReplies().isEmpty()) {
                        List<CommentResponse> replies = comment.getReplies().stream().map(reply -> {
                            CommentResponse replyResponse = new CommentResponse();
                            replyResponse.setCommentId(reply.getCommentId());
                            replyResponse.setUpdatedAt(reply.getUpdatedAt());
                            replyResponse.setContent(reply.getContent());
                            replyResponse.setCreateDate(reply.getCreateDate());
                            replyResponse.setUserId(reply.getUser().getId());
                            replyResponse.setUsername(reply.getUser().getUsername());
                            replyResponse.setUserImage(reply.getUser().getImageUrl());
                            return replyResponse;
                        }).collect(Collectors.toList());
                        response.setReplies(replies);  // 답글 리스트 추가
                    }

                    return response;
                }).collect(Collectors.toList());
    }

   


}