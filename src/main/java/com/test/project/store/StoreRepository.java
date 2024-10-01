package com.test.project.store;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.test.project.user.SiteUser;

public interface StoreRepository extends JpaRepository<Store, Integer> {

    @Query("SELECT s FROM Store s JOIN FETCH s.siteUser WHERE s.storeId = :storeId")
    Store findStoreWithUser(@Param("storeId") Integer storeId);
    
    // 사업자 이름으로 가게 검색 (페이지네이션 포함)
    Page<Store> findByStoreId(Integer storeId, Pageable pageable);
    Page<Store> findByStoreNameContainingIgnoreCase(String storeName, Pageable pageable);
    Page<Store> findBySiteUser_UsernameContainingIgnoreCase(String username, Pageable pageable);
    
 // 특정 SiteUser가 소유한 가게 리스트를 가져오는 메서드
    List<Store> findBySiteUser(SiteUser siteUser);
}
