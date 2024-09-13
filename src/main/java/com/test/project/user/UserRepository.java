package com.test.project.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<SiteUser, Long>{
	 Optional<SiteUser> findByUsername(String username);
	    Optional<SiteUser> findById(Long id);
	    
	    @Modifying
	    @Transactional
	    @Query("UPDATE SiteUser u SET u.chatRoom = :roomId WHERE u.id = :userId")
	    void updateChatRoom(@Param("userId") Long userId, @Param("roomId") String roomId);
}