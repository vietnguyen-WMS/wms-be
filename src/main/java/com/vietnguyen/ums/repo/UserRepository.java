package com.vietnguyen.ums.repo;

import com.vietnguyen.ums.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameIgnoreCase(String username);
    Optional<UserEntity> findByIdAndDeletedAtIsNull(Long id);
    Page<UserEntity> findAllByDeletedAtIsNull(Pageable pageable);
    boolean existsByUsernameIgnoreCase(String username);
}
