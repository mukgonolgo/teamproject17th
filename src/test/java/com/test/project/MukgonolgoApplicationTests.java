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
		
		Review r1 = new Review();
		r1.setSubject("진짜 맛집입니다");
		r1.setContent("추천드립니다");
		r1.setImageUrl("/img/food/food2_3.png");
		r1.setCreateDate(LocalDateTime.now());
		r1.setTag("맛집");
		this.reviewRepository.save(r1); //첫번째 질문 저장*/
		
		Review r2 = new Review();
		r2.setSubject("테스트입니다");
		r2.setContent("추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다추천드립니다");
		r2.setImageUrl("/img/food/food2_2.png");
		r2.setCreateDate(LocalDateTime.now());
		r2.setTag("맛집");
		this.reviewRepository.save(r2); //첫번째 질문 저장*/
		
		Review r3 = new Review();
		r3.setSubject("이미지 왜 안보여");
		r3.setContent("테스트입니다테스트입니다테스트입니다테스트입니다테스트입니다테스트입니다테스트입니다");
		r3.setImageUrl("/img/food/food2_3.png");
		r3.setCreateDate(LocalDateTime.now());
		r3.setTag("맛집");
		this.reviewRepository.save(r3); //첫번째 질문 저장*/
		

}
}
