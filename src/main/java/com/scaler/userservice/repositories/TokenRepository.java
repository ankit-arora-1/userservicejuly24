package com.scaler.userservice.repositories;

import com.scaler.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token save(Token token);

    Optional<Token> findByValueAndDeletedAndExpiryAtGreaterThan(
            String tokenValue,
            Boolean deleted,
                 Date date
    );

    Optional<Token> findByValueAndDeleted(String tokenValue, Boolean deleted);
}
