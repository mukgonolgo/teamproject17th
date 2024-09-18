package com.test.project.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {

    // 이름으로 SiteUser 찾기
    Optional<SiteUser> findByName(String name);

    // 이름과 이메일로 SiteUser 찾기 (아이디 찾기 용도)
    Optional<SiteUser> findByNameAndEmail(String name, String email);

    // 이름과 사용자 이름으로 SiteUser 찾기 (비밀번호 재설정 용도)
    Optional<SiteUser> findByNameAndUsername(String name, String username);

    // 사용자 이름으로 SiteUser 찾기
    Optional<SiteUser> findByUsername(String username);
    
    // 전화번호로 SiteUser 찾기
    Optional<SiteUser> findByPhoneNumber(String phoneNumber);
    
	    Optional<SiteUser> findById(Long id);
	    @Modifying
	    @Transactional
	    @Query("UPDATE SiteUser u SET u.chatRoom = :roomId WHERE u.id = :userId")
	    void updateChatRoom(@Param("userId") Long userId, @Param("roomId") String roomId);
}

