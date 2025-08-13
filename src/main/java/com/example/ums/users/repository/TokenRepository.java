package com.example.ums.users.repository;

import com.example.ums.users.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Find token by token value
     * @param tokenValue the token value to search for
     * @return Optional containing the token if found
     */
    Optional<Token> findByTokenValue(String tokenValue);

    /**
     * Find valid token by token value (not expired)
     * @param tokenValue the token value to search for
     * @return Optional containing the valid token if found
     */
    @Query("SELECT t FROM Token t WHERE t.tokenValue = :tokenValue AND t.expiresAt > :now")
    Optional<Token> findValidTokenByValue(@Param("tokenValue") String tokenValue, @Param("now") LocalDateTime now);

    /**
     * Delete expired tokens
     * @param now current time
     */
    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresAt <= :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Delete all tokens for a specific user
     * @param userId the user ID
     */
    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.id = :userId")
    void deleteTokensByUserId(@Param("userId") Long userId);
} 