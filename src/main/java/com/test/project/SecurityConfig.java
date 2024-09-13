	package com.test.project;
	
	import java.util.Arrays;
	import java.util.Collections;
	
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
	import org.springframework.web.cors.CorsConfiguration;
	
	@Configuration
	@EnableWebSecurity
	@EnableMethodSecurity(prePostEnabled = true)
	public class SecurityConfig {
	
	    @Bean
	    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf(csrf -> csrf
	                .disable()) // CSRF 보호 비활성화
	            .cors(cors -> cors.configurationSource(request -> {
	                CorsConfiguration corsConfig = new CorsConfiguration();
	                corsConfig.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
	                corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	                corsConfig.setAllowedHeaders(Arrays.asList("*"));
	                return corsConfig;
	            }))
	            .authorizeHttpRequests(requests -> requests
	                .requestMatchers("/ws/chat/**").authenticated() // WebSocket 엔드포인트 보호
	                .requestMatchers("/**").permitAll()) // 나머지 모든 요청 허용
	            .headers(headers -> headers
	                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
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
