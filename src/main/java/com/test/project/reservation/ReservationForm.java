package com.test.project.reservation;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationForm {
	@NotEmpty(message="날짜를 선택해 주세요")
	private String reservationDay;
	@NotEmpty(message="시간을 선택해 주세요")
	private String reservationtTime;
	@NotEmpty(message="인원수를 선택해 주세요")
	private String reservationMember;
}