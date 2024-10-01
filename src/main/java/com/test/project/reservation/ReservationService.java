package com.test.project.reservation;
import java.time.LocalDateTime;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.test.project.store.StoreService;
import com.test.project.DataNotFoundException;
import com.test.project.notice.Notice;
import com.test.project.store.Store;
import com.test.project.user.UserService;
import com.test.project.user.SiteUser;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;



@RequiredArgsConstructor
@Service
public class ReservationService {
	private final ReservationRepository reservationRepository;
	

	
	
	public Reservation create(Store store, String reservationDay, String reservationTime, String reservationMember, SiteUser user) {
	    Reservation reservation = new Reservation();
	    reservation.setReservationid(generateUniqueReservationId());
	    reservation.setStore(store);
	    reservation.setUser(user);
	    reservation.setReservationDay(reservationDay);
	    reservation.setReservationtime(reservationTime);
	    reservation.setReservationMember(reservationMember);

	    reservation.setCreateDate(LocalDateTime.now());
	    this.reservationRepository.save(reservation);
	    
	    return reservation;
	}

	public void modify(Reservation reservation, String reservationDay, String reservationTime, String reservationMember) {

	    reservation.setReservationDay(reservationDay);
	    reservation.setReservationtime(reservationTime);
	    reservation.setReservationMember(reservationMember);
		this.reservationRepository.save(reservation);
	}
    private Integer generateUniqueReservationId() {
        Random random = new Random();
        Integer id;
        do {
            id = random.nextInt(900000) + 100000; // 100000부터 999999까지 랜덤 숫자 생성
        } while (reservationRepository.existsByReservationid(id)); // 중복 확인
        return id;
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

	
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
    public Page<Reservation> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable);
    }

    public Optional<Reservation> getReservationById(Integer reservationid) {
        return reservationRepository.findById(reservationid);
    }
    


    public List<Reservation> getReservationsByUser(SiteUser user) {
        return reservationRepository.findByUser(user, Sort.by(Sort.Direction.DESC, "createDate"));
    }
    
    
	public Page<Reservation> getList(int page, String kw){
		//최신순으로 정렬
		List<Sort.Order> sorts= new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 7,Sort.by(sorts));
		return this.reservationRepository.findAllByKeyword(kw, pageable);
	}

    
    // 예약 삭제
    public void deleteReservation(Integer reservationid) {
        reservationRepository.deleteById(reservationid);
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
