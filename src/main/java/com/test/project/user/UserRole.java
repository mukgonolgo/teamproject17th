package com.test.project.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    BUSINESS("ROLE_BUSINESS"); // 사업자 역할 추가

    private String value;

    UserRole(String value) {
        this.value = value;
    }
}
