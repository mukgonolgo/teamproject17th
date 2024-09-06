package com.test.project.review;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId; //회원가입에서 가져올 id

    private Integer storeId; //식당정보에서 가져올 id

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewImage> images = new HashSet<>(); // 리뷰와 연관된 이미지들

    private String tag;

    private LocalDateTime uploadDate;

    private LocalDateTime modifyDate;

    private Integer rating;

    // private Integer likeCount; // 나중에

    // getters and setters
}
