package com.test.project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeHttpRequests(requests -> requests
	        	.requestMatchers(AntPathRequestMatcher.antMatcher("/comments/reviews/{reviewId}")).permitAll()  // 댓글 조회는 모두 허용
	            .requestMatchers(AntPathRequestMatcher.antMatcher("/comments/**")).authenticated()  // 댓글 작성/수정 등은 인증 필요
	            .anyRequest().permitAll()) // 그 외의 요청은 모두 허용
	        .csrf(csrf -> csrf
	            .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
	        .headers(headers -> headers
	            .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
	        .formLogin(form -> form
	            .loginPage("/user/login") // 로그인 페이지 경로
	            .defaultSuccessUrl("/")) // 로그인 성공 시 이동할 기본 경로
	        .logout(logout -> logout
	            .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
	            .logoutSuccessUrl("/")
	            .invalidateHttpSession(true)); // 로그아웃 시 세션 무효화
	    return http.build();
	}

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
