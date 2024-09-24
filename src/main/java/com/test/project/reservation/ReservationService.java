package com.test.project.reservation;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.test.project.store.StoreService;
import com.test.project.DataNotFoundException;
import com.test.project.store.Store;
import com.test.project.user.UserService;
import com.test.project.user.SiteUser;

import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	
	
	public Reservation create(Store store, String reservationDay, String reservationTime, String reservationMember, SiteUser author) {
	    Reservation reservation = new Reservation();
	    reservation.setStore(store);
	    reservation.setAuthor(author);
	    reservation.setReservationDay(reservationDay);
	    reservation.setReservationtime(reservationTime);
	    reservation.setReservationMember(reservationMember);
	    String reservationNumber;
	    do {
	        reservationNumber = generateRandomReservationNumber();
	    } while (isReservationNumberExists(reservationNumber)); // 중복 체크
	    
	    reservation.setReservationNumber(reservationNumber);
	    reservation.setCreateDate(LocalDateTime.now());
	    this.reservationRepository.save(reservation);
	    
	    return reservation;
	}
	
	// 랜덤한 6자리 숫자를 생성하는 메서드
	private String generateRandomReservationNumber() {
	    Random random = new Random();
	    int number = random.nextInt(900000) + 100000; // 100000부터 999999까지의 숫자 생성
	    return String.valueOf(number);
	}

	// reservationNumber의 중복 여부를 확인하는 메서드
	private boolean isReservationNumberExists(String reservationNumber) {
	    // 데이터베이스에서 reservationNumber가 존재하는지 확인하는 로직
	    return reservationRepository.existsByReservationNumber(reservationNumber);
	}
	
	
	//예약 조회
	public Reservation getReservation(Integer reservationid) {
		Optional<Reservation> store = this.reservationRepository.findById(reservationid);
		
		if(store.isPresent()) {
			return store.get();
		}else {
			throw new DataNotFoundException("답변이 없습니다.");
		}
	}
	



}
