package com.test.project.review;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.test.project.store.Store;
import com.test.project.user.SiteUser;

import org.springframework.data.repository.query.Param;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    /* 추가적인 쿼리 메서드 정의 가능
    Review findBySubject(String subject);

    Review findBySubjectAndContent(String subject, String content);

    List<Review> findBySubjectLike(String subject);*/
	List<Review> findByUserId(Long userId);
	
    // 리뷰 ID로 리뷰를 찾는 메소드
    Optional<Review> findById(Long id);
    
    // 최신순으로 정렬
    List<Review> findAllByOrderByCreateDateDesc();

    // 오래된순으로 정렬
    List<Review> findAllByOrderByCreateDateAsc();

    // 댓글 많은 순으로 정렬
    List<Review> findAllByOrderByCommentCountDesc();

    // 좋아요 많은 순으로 정렬
    List<Review> findAllByOrderByLikeCountDesc();
    
    List<Review> findByTitleContainingOrContentContaining(String title, String content);
    
    List<Review> findAllByStore_StoreId(Integer storeId);


 // 특정 스토어에 속한 리뷰의 개수를 반환하는 메소드
    long countByStore(Store store);
    
    
 // 최신순으로 상위 6개의 리뷰를 가져오는 쿼리
    List<Review> findTop6ByOrderByCreateDateDesc();
    
 // 지역을 포함하는 리뷰 검색 메서드  
    List<Review> findByStoreBasicAddressContaining(String region); 
    
    @Query("SELECT r FROM Review r WHERE " +
            "(r.title LIKE %:keyword% OR r.content LIKE %:keyword%) " +
            "OR (r.store IS NOT NULL AND (r.store.storeName LIKE %:keyword% OR r.store.basicAddress LIKE %:keyword%))")
     List<Review> searchReviewsByKeyword(@Param("keyword") String keyword);

}
