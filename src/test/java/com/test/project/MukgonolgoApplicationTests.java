package com.test.project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.test.project.review.Review;
import com.test.project.review.ReviewRepository;
import com.test.project.review.ReviewService;

@SpringBootTest
public class MukgonolgoApplicationTests {
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private ReviewService questionService;
	

	//@Transactional
	@Test
	void testJpa(){
		
	/*	Review r1 = new Review();
		r1.setSubject("진짜 맛집입니다");
		r1.setContent("추천드립니다");
		r1.setImageUrl("/img/food/food2_3.png");
		r1.setCreateDate(LocalDateTime.now());
		r1.setTag("맛집");
		this.reviewRepository.save(r1); //첫번째 질문 저장*/
		


}
}
