package com.test.project.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.test.project.chat.ChatRoom;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface SiteUserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findById(Long id);
    Optional<SiteUser> findByUsername(String username);
    Optional<SiteUser> findByEmail(String email);
    
    @Query("SELECT new com.test.project.user.UserDTO(u.name) FROM SiteUser u WHERE u.id = :id")
    UserDTO findUserNameById(@Param("id") Long id);

}
