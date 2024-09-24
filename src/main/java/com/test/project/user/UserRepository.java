package com.test.project.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser, Long> {

    // 이름으로 SiteUser 찾기
    Optional<SiteUser> findByName(String name);

    // 이름과 이메일로 SiteUser 찾기
    Optional<SiteUser> findByNameAndEmail(String name, String email);

    // 이름과 사용자 이름으로 SiteUser 찾기
    Optional<SiteUser> findByNameAndUsername(String name, String username);

    // 사용자 이름으로 SiteUser 찾기
    Optional<SiteUser> findByUsername(String username);
    
 // 닉네임으로 SiteUser 찾기
    Optional<SiteUser> findByNickname(String nickname);

   
}

