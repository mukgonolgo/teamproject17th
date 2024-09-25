package com.test.project.review.tag;

import java.util.Set;

import com.test.project.review.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review_tag_map")
public class ReviewTagMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne
    @JoinColumn(name = "review_tag_id", nullable = false)
    private ReviewTag reviewTag;
    
    // 태그를 정렬된 순서로 가져오기 위해 추가
    @Column(name = "order_index")
    private Integer orderIndex;
    
  
}
