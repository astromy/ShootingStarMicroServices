package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,Long> {
}
