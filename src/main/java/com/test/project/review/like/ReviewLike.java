package com.test.project.review.like;

import com.test.project.review.Review;
import com.test.project.user.SiteUser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ReviewLike {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "review_id")
	    private Review review;

	    @ManyToOne
	    @JoinColumn(name = "user_id")
	    private SiteUser user;
}
