package com.test.project.reservation;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.test.project.store.Store;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	 boolean existsByReservationNumber(String reservationNumber);
	 
	// 예약 ID로 검색 (여러 Store에 대해서)
	 Page<Reservation> findByStoreInAndReservationid(List<Store> store, Integer reservationid, Pageable pageable);

	    
	    Page<Reservation> findByStoreInAndStore_StoreNameContaining(List<Store> stores, String storeName, Pageable pageable);

	    
	 // 사용자가 등록한 가게의 예약 리스트 가져오기
	    Page<Reservation> findByStoreIn(Set<Store> stores, Pageable pageable);

	    // 가게 목록에 대한 예약을 가져오는 메서드
	    Page<Reservation> findByStoreIn(List<Store> stores, Pageable pageable);
	    
}