package com.test.project.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.test.project.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @Column(name = "board_username",  length = 50)
    private String username;

    
    public String getUsername() {
        return user != null ? user.getUsername() : null; // user가 null이 아닐 때만 username 반환
    }
    
	
	@Column( length = 100, columnDefinition = "TEXT")
	private String AnswerContent;
	
   
    private LocalDateTime AnswerCreateDate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Board board;
    
  
    @ManyToOne(fetch = FetchType.EAGER)
     private Answer parentAnswer; 
	
    
    @OneToMany(mappedBy = "parentAnswer", cascade = CascadeType.ALL)    
    private List<Answer> comment = new ArrayList<>(); // 대댓글 목록
    
 
}