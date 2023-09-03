package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.util;

import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model.Users;
import com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersUtil {
    private final UsersRepository usersRepository;
    private final WebClient.Builder webClientBuilder;
    public static List<Users> usersGlobalList=new ArrayList<>();
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetchAllUsers() {
        usersGlobalList = usersRepository.findAll();
        log.info("{} users RECORDS FETCHED", usersGlobalList.size());
    }
}
