package com.vietnguyen.ums.service;

import com.vietnguyen.ums.dto.UserInfo;
import com.vietnguyen.ums.dto.UserResponse;
import com.vietnguyen.ums.entity.UserEntity;

public class UserMapper {
    public static UserInfo toInfo(UserEntity entity, String status, String role) {
        return new UserInfo(entity.getId(), entity.getUsername(), status, role, entity.getLastLoginAt());
    }

    public static UserResponse toResponse(UserEntity entity, String status, String role) {
        return new UserResponse(entity.getId(), entity.getUsername(), status, role,
                entity.getLastLoginAt(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
