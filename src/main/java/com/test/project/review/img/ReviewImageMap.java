package com.test.project.review.img;

import com.test.project.review.Review;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "review_img_map")
public class ReviewImageMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "review_image_id", nullable = false)
    private ReviewImage reviewImage;
    
    // ReviewImage를 반환하는 메서드 추가
    public ReviewImage getImage() {
        return reviewImage;  // 필드 이름이 reviewImage이므로 이것을 반환
    }


    
 
}
