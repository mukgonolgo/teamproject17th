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
    
    List<Store> findByVoterContaining(SiteUser user);
}
