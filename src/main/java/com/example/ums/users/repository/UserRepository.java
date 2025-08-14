package com.example.ums.users.repository;

import com.example.ums.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if user exists by username
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);

    @Query("""
            SELECT u FROM User u 
            WHERE (:includeDeleted = true OR u.status <> 'DELETED') 
              AND (:q IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<User> search(@Param("q") String q,
                      @Param("includeDeleted") boolean includeDeleted,
                      Pageable pageable);
}