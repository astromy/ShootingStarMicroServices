package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.redis.TokensEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokensRedisRepository extends CrudRepository<TokensEntity, String> {
}
