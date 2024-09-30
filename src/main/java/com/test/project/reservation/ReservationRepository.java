package com.test.project.reservation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Sort;

import com.test.project.notice.Notice;
import com.test.project.user.SiteUser;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	 
	 boolean existsByReservationid(Integer reservationId);
	 List<Reservation> findByUser(SiteUser user, Sort sort);
	 
	    // 제목으로 공지를 검색하는 메소드
	    List<Reservation> findByReservationidContaining(String reservationid);
	    Page<Reservation> findAll(Pageable pageable);
	    // 키워드로 공지를 검색하는 메소드
	    @Query("SELECT r FROM Reservation r WHERE r.reservationid LIKE %:kw%")
		Page<Reservation> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
	 
}