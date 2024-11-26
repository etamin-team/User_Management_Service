package com.example.user_management_service.repository;

import com.example.user_management_service.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Date-11/20/2024
 * By Sardor Tokhirov
 * Time-1:10 AM (GMT+5)
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
            select t from Token t inner join User s on t.user.userId = s.userId where s.userId = :userId and (t.expired =false or t.revoked =false)
            """)
    List<Token> findByValidTokensBySupplier(UUID userId);

    Optional<Token> findByToken(String token);
}