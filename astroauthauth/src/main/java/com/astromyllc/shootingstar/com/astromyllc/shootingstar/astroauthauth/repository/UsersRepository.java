package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsersRepository extends JpaRepository<Users,Long> {

    public Optional<Users> findUserByUsername(String userName);
    @Query("SELECT u FROM Users u where u.email=:username")
    Optional<Users> findUserByEmail(@Param("username")String username);

}
