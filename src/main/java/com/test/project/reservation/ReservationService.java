package com.test.project.reservation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	
	
	public Reservation create(Store store, String reservationDay, String reservationTime, String reservationMember, SiteUser user) {
	    Reservation reservation = new Reservation();
	    reservation.setStore(store);
	    reservation.setUser(user);
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
	
	public Page<Reservation> searchByReservationId(List<Store> stores, Integer reservationid, Pageable pageable) {
	    return reservationRepository.findByStoreInAndReservationid(stores, reservationid, pageable);
	}



	public Page<Reservation> searchByStoreName(List<Store> stores, String storeName, Pageable pageable) {
	    return reservationRepository.findByStoreInAndStore_StoreNameContaining(stores, storeName, pageable);
	}


    // 전체 예약 리스트 조회
    public Page<Reservation> getReservationList(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

 // 가게 리스트에 해당하는 예약 정보 가져오기
    public Page<Reservation> getReservationsByStores(List<Store> stores, Pageable pageable) {
        return reservationRepository.findByStoreIn(stores, pageable);
    }
	
 // 사용자별 예약 조회
    public Page<Reservation> getReservationsByUser(SiteUser user, Pageable pageable) {
        return reservationRepository.findByUser(user, pageable);
    }

    // 사용자와 예약 ID로 검색
    public Page<Reservation> searchByUserAndReservationId(SiteUser user, Integer reservationId, Pageable pageable) {
        return reservationRepository.findByUserAndReservationid(user, reservationId, pageable);
    }

    // 사용자와 가게 이름으로 검색
    public Page<Reservation> searchByUserAndStoreName(SiteUser user, String storeName, Pageable pageable) {
        return reservationRepository.findByUserAndStore_StoreNameContaining(user, storeName, pageable);
    }



}
