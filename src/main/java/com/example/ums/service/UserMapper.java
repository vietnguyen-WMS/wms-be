package com.example.ums.service;

import com.example.ums.dto.UserInfo;
import com.example.ums.dto.UserResponse;
import com.example.ums.entity.UserEntity;

public class UserMapper {
    public static UserInfo toInfo(UserEntity entity, String status) {
        return new UserInfo(entity.getId(), entity.getUsername(), status, entity.getLastLoginAt());
    }

    public static UserResponse toResponse(UserEntity entity, String status) {
        return new UserResponse(entity.getId(), entity.getUsername(), status,
                entity.getLastLoginAt(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
