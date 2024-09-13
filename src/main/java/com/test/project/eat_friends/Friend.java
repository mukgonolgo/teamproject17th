package com.test.project.eat_friends;

import java.util.UUID;

import com.test.project.chat.ChatRoom;
import com.test.project.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId; // 게시글의 ID값

    @Column(nullable = false, length = 20)
    private String friendTitle; // 제목 (20자, 빈칸 금지)

    @Column(nullable = false) 
    private Integer friendMaxMembers; // 밥친구의 최대 인원수  

    @Column(nullable = false)
    private Integer friendTotalMembers; // 밥친구의 총 인원수  

    @Column(nullable = false)
    private String friendEventDate; // 만나는 날

    @Column(nullable = false)
    private String friendEventTime; // 만나는 시간
    
    @Column(nullable = false, unique = true) 
    private String roomID; // 채팅방 ID

    @Column(length = 100)
    private String friendMemberName; // 작성자 이름
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SITE_USER_ID")
    private SiteUser siteUser; // 게시글(many) to 작성자(one)의 관계로 해당 테이블의 데이터를 가져옴

    // 생성자를 통해 roomID 생성
    public Friend() {
        this.roomID = UUID.randomUUID().toString(); // 고유한 roomID 생성
    }

    // 게시글 ID getter/setter 수동 설정
    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    // 나머지 필드에 대한 getter/setter는 Lombok이 자동으로 생성
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;
}
