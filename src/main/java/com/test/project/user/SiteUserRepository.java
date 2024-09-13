package com.test.project.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.test.project.chat.ChatRoom;

@Repository
public interface SiteUserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findById(Long id);
    Optional<SiteUser> findByUsername(String username);
    Optional<SiteUser> findByEmail(String email);
    
    @Modifying
    @Query("UPDATE SiteUser u SET u.chatRoom = :chatRoom WHERE u.id = :userId")
    void updateChatRoom(Long userId, ChatRoom chatRoom);

}
