package com.test.project.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SiteUser> _siteUser = this.userRepository.findByUsername(username);
        if (_siteUser.isEmpty()) {
            throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.");
        }

        SiteUser siteUser = _siteUser.get();

        // 승인 상태 확인
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (siteUser.getApprovalStatus() == 0) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue())); // 관리자 권한 부여
        } else if (siteUser.getApprovalStatus() == 1) {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue())); // 일반 사용자 권한 부여
        } else if (siteUser.getApprovalStatus() == 2) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PENDING")); // 승인 대기
        } else if (siteUser.getApprovalStatus() == 3) {
            authorities.add(new SimpleGrantedAuthority(UserRole.BUSINESS.getValue())); // 승인된 사업자 권한 부여
        } else if (siteUser.getApprovalStatus() == 4) {
            authorities.add(new SimpleGrantedAuthority("ROLE_HOLD")); // 보류 상태
        } else {
            throw new UsernameNotFoundException("유효하지 않은 승인 상태입니다.");
        }

        // CustomUserDetails 사용
        return new CustomUserDetails(siteUser, authorities);
    }


}
