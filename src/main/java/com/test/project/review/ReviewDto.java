package com.test.project.review;

import java.time.LocalDateTime;
import java.util.List;

import com.test.project.review.comment.ReviewComment;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.tag.ReviewTagMap;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ReviewDto {
    private Long id;
    private String title;
    private String content;
    private String rating;
    private LocalDateTime createDate;
    private String userName;
    private List<ReviewImageMap> reviewImageMap;
    private List<ReviewComment> commentList;
    private List<ReviewTagMap> tagMaps;
    private Long likeCount;
    private boolean likedByUser;

    // Review 엔티티를 ReviewDto로 변환하는 기존 생성자
    public ReviewDto(Review review) {
        this.id = review.getId();
        this.title = review.getTitle();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.createDate = review.getCreateDate();
        this.userName = review.getUser().getUsername();  // 유저 이름을 포함
        this.reviewImageMap = review.getReviewImageMap();
        this.commentList = review.getCommentList();
        this.tagMaps = List.copyOf(review.getTagMaps());
        this.likeCount = review.getLikeCount();
        this.likedByUser = review.isLikedByUser();
    }

    // 새로운 생성자: Review, likedByUser, likeCount를 매개변수로 받음
    public ReviewDto(Review review, boolean likedByUser, Long likeCount) {
        this.id = review.getId();
        this.title = review.getTitle();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.createDate = review.getCreateDate();
        this.userName = review.getUser().getUsername();  // 유저 이름을 포함
        this.reviewImageMap = review.getReviewImageMap();
        this.commentList = review.getCommentList();
        this.tagMaps = List.copyOf(review.getTagMaps());
        this.likeCount = likeCount;  // 새로운 likeCount 할당
        this.likedByUser = likedByUser;  // 새로운 likedByUser 할당
    }
}
