package com.test.project.review.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewTagMapRepository extends JpaRepository<ReviewTagMap, Long> {
    // 추가적인 쿼리 메소드가 필요하다면 여기에 정의
}
