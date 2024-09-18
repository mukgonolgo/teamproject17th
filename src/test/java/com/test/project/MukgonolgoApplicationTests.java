package com.test.project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.review.Review;
import com.test.project.review.img.ReviewImage;
import com.test.project.user.SiteUser;
import com.test.project.user.UserRepository;
import com.test.project.review.ReviewRepository;
import com.test.project.review.ReviewService;
import com.test.project.review.comment.ReviewComment;
import com.test.project.review.comment.ReviewCommentService;

@SpringBootTest
public class MukgonolgoApplicationTests {
    
//	  @Autowired
//	    private ReviewCommentService reviewCommentService;
//
//	    @Autowired
//	    private ReviewRepository reviewRepository;
//
//	    @Autowired
//	    private UserRepository siteUserRepository;
//	    @Test
//	    public void testAddComment() {
//	        // SiteUser를 생성할 때, 정의한 생성자 또는 빌더를 사용합니다.
//	        SiteUser user = new SiteUser();
//	        Review review = reviewRepository.save(new Review());
//
//	        ReviewComment comment = reviewCommentService.addComment("좋은 리뷰입니다!", user, review);
//
//	        assertNotNull(comment);
//	        assertEquals("좋은 리뷰입니다!", comment.getComment());
//	        assertEquals(review, comment.getReview());
//	        assertEquals(user, comment.getUser());
//	    }
//
//
//	    @Test
//	    public void testAddReply() {
//	        SiteUser user = siteUserRepository.save(new SiteUser());
//	        Review review = reviewRepository.save(new Review());
//	        ReviewComment parentComment = reviewCommentService.addComment("좋은 리뷰입니다!", user, review);
//
//	        ReviewComment reply = reviewCommentService.addReply(parentComment.getCommentId(), "대댓글입니다!", user);
//
//	        assertNotNull(reply);
//	        assertTrue(reply.isReply());
//	        assertEquals("대댓글입니다!", reply.getComment());
//	        assertEquals(parentComment, reply.getParent());
//	        assertEquals(review, reply.getReview());
//	    }
	}