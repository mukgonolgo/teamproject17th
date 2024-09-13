package com.test.project.chat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.test.project.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatRoom {

    @Id
    private String roomId;

    @Column(name = "name")
    private String name;

    @PrePersist
    public void generateRoomId() {
        if (this.roomId == null) {
            this.roomId = UUID.randomUUID().toString();
        }
    }

    @Column(name = "chatmax_members", nullable = false)
    private int chatmaxMembers = 30; // 기본값 설정
    
    @Column(name = "chatcurrent_members", nullable = false)
    private int chatcurrentMembers = 0;  // 현재 인원 수, 기본값 설정

    
    @ManyToMany
    @JoinTable(
        name = "chatroom_members",
        joinColumns = @JoinColumn(name = "chatroom_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<SiteUser> members = new HashSet<>();
}
