package com.vietnguyen.ums.repo;

import com.vietnguyen.ums.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Short> {
    Optional<UserRoleEntity> findByCode(String code);
}
