package com.test.project.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
	 boolean existsByReservationNumber(String reservationNumber);
	 

}