package com.test.project.review.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {
    ReviewTag findByName(String name);
}
