package com.test.project.reservation;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.test.project.store.StoreService;
import com.test.project.notice.Notice;
import com.test.project.store.Store;
import com.test.project.user.UserService;
import com.test.project.user.SiteUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/reservation")
@RequiredArgsConstructor
@Controller
public class ReservationController {
	private final StoreService storeService;
	private final ReservationService reservationService;
	private final UserService userService;

//	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{StoreId}")
	public String createAnswer(Model model, @PathVariable("StoreId") Integer StoreId,
			@Valid ReservationForm reservationForm, BindingResult bindingResult, Principal principal) {
		// 현재 로그인 한 사용자의 정보를 알려면 스프링 시큐리티가 제공하는 Principal(본인) 객체를 사용해야 한다.Principal의
		// getName()를 이용하면 현재 로그인한 사용자ID를 알 수 있다.
		Store store = this.storeService.getStore(StoreId);
		SiteUser siteUser = this.userService.getUser(principal.getName());		
		  if(bindingResult.hasErrors()) { 
			  model.addAttribute("store", store); 
		  return "store/store_detail"; }
		 
		Reservation reservation = reservationService.create(store, reservationForm.getReservationDay(),
				reservationForm.getReservationtTime(), reservationForm.getReservationMember(), siteUser);
		return "redirect:/reservation/completed/"+ reservation.getReservationid();
	}
//예약 상태창
	@GetMapping("/completed/{reservationid}")
	public String detail(Model model, @PathVariable("reservationid") Integer reservationid) {
		Reservation reservation = reservationService.getReservation(reservationid);
	    if (reservation.getUser() != null) {
	    	model.addAttribute("re_user", reservation.getUser()); 
	    }
	    if (reservation.getStore() != null) {
	        model.addAttribute("re_store", reservation.getStore()); 
	    }
	    

		model.addAttribute("reservation", reservation);
		return "reservation/reservation_completed";
	}
	
//	수정창 이동
	@GetMapping("/modify/{reservationid}")
	public String reservationModify(Model model, @PathVariable("reservationid") Integer reservationid, Principal principal) {
		Reservation reservation = reservationService.getReservation(reservationid);
	    if (reservation.getStore() != null) {
	        model.addAttribute("store", reservation.getStore()); 
	    }

		model.addAttribute("reservation", reservation);
		return "reservation/reservation_form";
	}
    
//    예약 수정
	@PostMapping("/modify/{reservationid}")
	public String reservationModify(@Valid ReservationForm reservationForm, BindingResult bindingResult,
	                                 @PathVariable("reservationid") Integer reservationid, Principal principal) {
	    try {
	        System.out.println("Received reservation ID: " + reservationid); // 로그 추가
	        
	        Reservation reservation = this.reservationService.getReservation(reservationid);
	        
	        // 수정 권한 체크 (주석 해제 후 사용)
	        // if(!reservation.getUser().getUsername().equals(principal.getName())) {
	        //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
	        // }
	        
	        this.reservationService.modify(reservation, reservationForm.getReservationDay(),
	                reservationForm.getReservationtTime(), reservationForm.getReservationMember());
	        
	        return String.format("redirect:/reservation/completed/%s", reservationid);
	    } catch (Exception e) {
	        e.printStackTrace(); // 예외 로그 출력
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "오류 발생");
	    }
	}



}

    

    
    