package com.test.project.review;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.project.user.SiteUser;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /* 추가적인 쿼리 메서드 정의 가능
    Review findBySubject(String subject);

    Review findBySubjectAndContent(String subject, String content);

    List<Review> findBySubjectLike(String subject);*/
	List<Review> findByUserId(Long userId);
}
