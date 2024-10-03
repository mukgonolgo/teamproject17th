package com.test.project.review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.like.ReviewLike;
import com.test.project.review.tag.ReviewTagMap;
import com.test.project.store.Store;
import com.test.project.review.comment.ReviewComment;  // 댓글 클래스 import
import com.test.project.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;
    
    @Column(length = 100, name = "review_title")
    private String title;
    
    @Column(columnDefinition = "TEXT", name = "review_content")
    private String content;
    
    @Column(name = "rating")
    private String rating;

    @Column(name = "create_date")
    private LocalDateTime createDate;
    
    @Column(name = "updated_At")
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private SiteUser user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;


    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImageMap> reviewImageMap = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    private Set<ReviewTagMap> tagMaps = new HashSet<>();

    @OneToMany(mappedBy = "review",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createDate ASC")
    private List<ReviewComment> commentList = new ArrayList<>();

    @Column(name = "comment_count")
    private Long commentCount = 0L;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewLike> likes = new ArrayList<>();
    
    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Transient
    private boolean likedByUser;  // 사용자가 좋아요를 눌렀는지 여부

    // 좋아요 추가
    public void addLike(ReviewLike like) {
        this.likes.add(like);
        this.likeCount++;  // 좋아요 수 증가
    }

    // 좋아요 제거 (음수 방지 포함)
    public void removeLike(ReviewLike like) {
        this.likes.remove(like);
        if (this.likeCount > 0) {
            this.likeCount--;  // 좋아요 수 감소
        }
    }

    // 댓글 수 업데이트
    public void updateCommentCount() {
        this.commentCount = (long) this.commentList.size();
    }

    // 이미지 리스트 반환
    public List<ReviewImage> getImages() {
        List<ReviewImage> images = new ArrayList<>();
        for (ReviewImageMap imageMap : reviewImageMap) {
            images.add(imageMap.getImage());
        }
        return images;
    }

    // 좋아요 리스트에서 수를 카운트할 수도 있음
    public void updateLikeCount() {
        this.likeCount = (long) this.getLikes().size();
    }
}
