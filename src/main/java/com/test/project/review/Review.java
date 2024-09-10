package com.test.project.review;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
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
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "rating")
    private String rating;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC") // 태그를 정렬된 순서로 가져오기
    private Set<ReviewTagMap> tagMaps = new HashSet<>();
}
