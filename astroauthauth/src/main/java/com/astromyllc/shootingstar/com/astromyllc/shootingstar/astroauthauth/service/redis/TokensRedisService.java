package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.service.redis;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.redis.TokensEntity;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository.TokensRedisRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TokensRedisService {

    private final TokensRedisRepository tokensRedisRepository;
    public TokensEntity save(TokensEntity entity) {
        return tokensRedisRepository.save(entity);
    }

    public Optional<TokensEntity> findById(String id) {
        return tokensRedisRepository.findById(id);
    }
    public Iterable<TokensEntity> findAll() {
        return tokensRedisRepository.findAll();
    }


}
