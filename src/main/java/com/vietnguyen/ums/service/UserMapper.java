package com.vietnguyen.ums.service;

import com.vietnguyen.ums.dto.UserInfo;
import com.vietnguyen.ums.dto.UserResponse;
import com.vietnguyen.ums.entity.UserEntity;
import com.vietnguyen.ums.entity.UserInfoEntity;

public class UserMapper {
    public static UserInfo toInfo(UserEntity entity, UserInfoEntity info, String status, String role) {
        return new UserInfo(entity.getId(), entity.getUsername(), status, role, entity.getLastLoginAt(),
                info != null ? info.getDisplayName() : null,
                info != null ? info.getAvatarUrl() : null,
                info != null ? info.getBio() : null,
                info != null ? info.getAddress() : null);
    }

    public static UserResponse toResponse(UserEntity entity, String status, String role) {
        return new UserResponse(entity.getId(), entity.getUsername(), status, role,
                entity.getLastLoginAt(), entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
