package com.test.project.review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.tag.ReviewTagMap;
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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review")  // 이 클래스가 "review" 테이블과 매핑됨
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ID 값을 자동으로 생성
    @Column(name = "review_id")
    private Long id;
    
    @Column(length = 100, name = "review_title")  // 리뷰 제목 (최대 길이 100자)
    private String title;
    
    @Column(columnDefinition = "TEXT", name = "review_content")  // 리뷰 내용 (TEXT 타입)
    private String content;
    
    @Column(name = "rating")  // 리뷰 평점
    private String rating;

    @Column(name = "create_date")  // 리뷰 생성일
    private LocalDateTime createDate;
    
    @ManyToOne
    @JoinColumn(name = "user_id")  // 외래키를 "user_id"로 설정
    private SiteUser user;  // 작성자 (SiteUser와 연관)

    // 이미지와의 관계 설정
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImageMap> reviewImageMap = new ArrayList<>();

    // ReviewImageMap을 설정하는 메서드
    public void setReviewImageMap(List<ReviewImageMap> reviewImageMap) {
        this.reviewImageMap = reviewImageMap;
    }

    // 태그 맵핑을 위한 설정
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")  // 태그들을 orderIndex 기준으로 정렬하여 가져옵니다.
    private Set<ReviewTagMap> tagMaps = new HashSet<>();

    // 댓글 및 대댓글과의 관계 설정
    @OneToMany(mappedBy = "review",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createDate ASC")  // 생성일 기준으로 댓글을 정렬
    private List<ReviewComment> commentList = new ArrayList<>();
    
    

}