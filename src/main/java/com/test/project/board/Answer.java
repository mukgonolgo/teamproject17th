package com.test.project.board;

import java.time.LocalDateTime;

import com.test.project.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long AnswerId;
	
	@ManyToOne
	private SiteUser user;
	
    // 추가된 username 필드
    @Column(name = "board_username", nullable = false, length = 50)
    private String username;

    
    public String getUsername() {
        return user != null ? user.getUsername() : null; // user가 null이 아닐 때만 username 반환
    }
    
	
	@Column(nullable=false, length = 100, columnDefinition = "TEXT")
	private String AnswerContent;
	
    @Column(nullable = false)
    private LocalDateTime AnswerCreateDate;
    
    @ManyToOne
    private Board board;

	
}
