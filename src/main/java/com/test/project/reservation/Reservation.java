package com.test.project.reservation;

import jakarta.persistence.JoinColumn; // 추가
import jakarta.persistence.JoinTable; // 추가
import jakarta.persistence.ManyToMany; // 추가
import jakarta.persistence.ManyToOne; // 추가

import java.time.LocalDateTime;
import java.util.Set;

import com.test.project.store.Store;
import com.test.project.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity; // 추가
import jakarta.persistence.GeneratedValue; // 추가
import jakarta.persistence.GenerationType; // 추가
import jakarta.persistence.Id; // 추가
import lombok.Getter; // 추가
import lombok.Setter; // 추가

@Getter
@Setter
@Entity
public class Reservation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reservationid;
	
	private String reservationNumber;
	@Column(columnDefinition = "TEXT")
	private String reservationDay;
	@Column(columnDefinition = "TEXT")
	private String reservationtime;
	@Column(columnDefinition = "TEXT")
	private String reservationMember;
	private LocalDateTime createDate;
	@ManyToOne
    @JoinColumn(name = "store_id") 
	private Store store; //외래키(FK) 관계 : Answer(자식:N)<-Question(부모:1) 
	@ManyToOne
    @JoinColumn(name = "user_id") 
	private SiteUser user;
}
