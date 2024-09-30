package com.test.project;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailureHandler;


  
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
	    .authorizeHttpRequests(requests -> requests
	    		   .requestMatchers(AntPathRequestMatcher.antMatcher("/comments/reviews/**")).permitAll() // 인증 없이도 댓글 조회 가능
	               .anyRequest().permitAll()) // 나머지 요청도 모두 허용
	    .csrf(csrf -> csrf
	        .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")))
	    .headers(headers -> headers
	        .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
	        .cacheControl(cache -> cache.disable()))  // 캐시 비활성화
	    .formLogin(form -> form
	        .loginPage("/user/login")
	        .defaultSuccessUrl("/"))
	    .logout(logout -> logout
	        .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
	        .logoutSuccessUrl("/")
	        .invalidateHttpSession(true));

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
