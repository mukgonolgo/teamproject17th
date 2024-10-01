package com.test.project.reservation;

import java.util.List;

import java.util.Set;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import com.test.project.notice.Notice;
import com.test.project.user.SiteUser;

import com.test.project.store.Store;
import com.test.project.user.SiteUser;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	 boolean existsByReservationid(Integer reservationId);
	 List<Reservation> findByUser(SiteUser user, Sort sort);

	// 예약 ID로 검색 (여러 Store에 대해서)
	 Page<Reservation> findByStoreInAndReservationid(List<Store> store, Integer reservationid, Pageable pageable);

	    
	    Page<Reservation> findByStoreInAndStore_StoreNameContaining(List<Store> stores, String storeName, Pageable pageable);

	    
	 // 사용자가 등록한 가게의 예약 리스트 가져오기
	    Page<Reservation> findByStoreIn(Set<Store> stores, Pageable pageable);

	    // 가게 목록에 대한 예약을 가져오는 메서드
	    Page<Reservation> findByStoreIn(List<Store> stores, Pageable pageable);
	    
	 // 사용자별 예약 조회
	    Page<Reservation> findByUser(SiteUser user, Pageable pageable);

	    // 사용자와 예약 ID로 검색
	    Page<Reservation> findByUserAndReservationid(SiteUser user, Integer reservationId, Pageable pageable);

	    // 사용자와 가게 이름으로 검색
	    Page<Reservation> findByUserAndStore_StoreNameContaining(SiteUser user, String storeName, Pageable pageable);

	    


	 

}