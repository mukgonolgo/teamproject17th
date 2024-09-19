package com.test.project.review.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import com.test.project.user.SiteUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.test.project.review.Review;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ReviewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @NotEmpty(message = "댓글 내용은 비어 있을 수 없습니다.")
    private String content;

    // 댓글이면 false, 대댓글이면 true
    private boolean reply;  // isReply에서 reply로 변경

    // 대댓글 순서로 데이터 정렬할 때 필요
    private int orders;

    // 한개의 댓글과 그에 딸린 대댓글들을 한 그룹으로 묶는다.
    private int groups;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ReviewComment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewComment> replies = new ArrayList<>();

    // 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private SiteUser user;

    // 게시물
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    @JsonIgnore
    private Review review;

    // 댓글 작성 시간 필드
    private LocalDateTime createDate;

    // 댓글 수정 시간 필드
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createDate = LocalDateTime.now();
        this.updatedAt = this.createDate; // 생성 시점에 updatedAt도 초기화
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

 
}
