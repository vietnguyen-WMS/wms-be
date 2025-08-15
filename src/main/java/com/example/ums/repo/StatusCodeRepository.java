package com.example.ums.repo;

import com.example.ums.entity.StatusCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusCodeRepository extends JpaRepository<StatusCodeEntity, Short> {
    Optional<StatusCodeEntity> findByDomainAndCode(String domain, String code);
}
