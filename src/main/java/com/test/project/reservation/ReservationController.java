package com.test.project.reservation;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

	@GetMapping("/list")
	@PreAuthorize("isAuthenticated()")
	public String list(Model model, 
	                   @RequestParam(value = "page", defaultValue = "0") int page,
	                   @RequestParam(value = "searchType", required = false) String searchType,
	                   @RequestParam(value = "search", required = false) String search,
	                   Principal principal) {

	    // 로그인한 사용자 정보를 가져옴
	    SiteUser siteUser = userService.getUser(principal.getName());

	    // 사용자가 소유한 가게들을 가져옴
	    List<Store> stores = storeService.getStoresByOwner(siteUser);

	    // 페이징 설정
	    Pageable pageable = PageRequest.of(page, 10);

	    // 검색어와 검색 유형에 따른 예약 리스트 필터링
	    Page<Reservation> reservationPage;

	    try {
	        if (search != null && !search.isEmpty()) {
	            if ("reservationId".equals(searchType)) {
	                // 예약 ID로 검색 (String -> Integer 변환)
	                Integer reservationIdInt = Integer.parseInt(search);
	                reservationPage = reservationService.searchByReservationId(stores, reservationIdInt, pageable);
	            } else if ("storeName".equals(searchType)) {
	                // 가게 이름으로 검색
	                reservationPage = reservationService.searchByStoreName(stores, search, pageable);
	            } else {
	                // 기본 예약 리스트 가져오기 (검색어 없음)
	                reservationPage = reservationService.getReservationsByStores(stores, pageable);
	            }
	        } else {
	            // 기본 예약 리스트 가져오기 (검색어 없음)
	            reservationPage = reservationService.getReservationsByStores(stores, pageable);
	        }
	    } catch (NumberFormatException e) {
	        // 예약 ID 검색 시, 잘못된 형식의 예약 ID 입력 처리
	        reservationPage = reservationService.getReservationsByStores(stores, pageable);
	        model.addAttribute("searchError", "예약 ID는 숫자여야 합니다.");
	    }

	    // 모델에 예약 정보를 추가
	    model.addAttribute("reservationPage", reservationPage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", reservationPage.getTotalPages());
	    model.addAttribute("searchType", searchType);
	    model.addAttribute("search", search);

	    return "reservation/reservation_list";
	}





}
