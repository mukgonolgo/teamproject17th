package com.test.project.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//로그인된 user의 처리를 담당하는 컨트롤러

@RequestMapping("/api/user")
@RestController
public class UserApiController {
    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        SiteUser user = userService.getUserById(userId);
        if (user != null) {
            UserResponseDto userResponseDto = new UserResponseDto(user.getUsername(), user.getId());
            return ResponseEntity.ok(userResponseDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 사용자 정보 응답을 위한 DTO
    public static class UserResponseDto {
        private String username;
        private Long id;

        public UserResponseDto(String username, Long id) {
            this.username = username;
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public Long getId() {
            return id;
        }
    }
}
