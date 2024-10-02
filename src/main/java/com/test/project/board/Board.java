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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long boardId;

    @ManyToOne(fetch = FetchType.EAGER)	
    @JoinColumn(name = "user_id")
    private SiteUser user;

    
    // 추가된 username 필드
    @Column(name = "board_username", nullable = false, length = 50)
    private String username;

    
    public String getUsername() {
        return user != null ? user.getUsername() : null; // user가 null이 아닐 때만 username 반환
    }
    
    
    @Column(name = "board_title", nullable = false, length = 50)
    private String boardTitle;

    @Column(name = "board_content", nullable = false, columnDefinition = "TEXT")
    private String boardContent;

    
    @Column(name = "board_create_date", nullable = false)
    private LocalDateTime boardCreateDate;
    
    @PrePersist
    protected void onCreate() {
        this.boardCreateDate = LocalDateTime.now(); // 자동으로 현재 시간을 설정
    }



    @Column(name = "board_tag")
    private String boardTag;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)   
    private List<Answer> answerList = new ArrayList<>();
   
    
    private boolean isPrivate; //비밀글 여부
}