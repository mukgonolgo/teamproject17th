package com.test.project.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

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
    
    // 사용자 ID로 SiteUser 찾기 - 지원
    Optional<SiteUser> findById(Long id);
}
