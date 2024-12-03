package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.model.Parents;
import com.astromyllc.shootingstar.adminpta.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ParentsUtil {

    private final ParentRepository parentRepository;
    public static List<Parents> parentGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetAllParents() {
        parentGlobalList = parentRepository.findAll();
        log.info("Global Parents List populated with {} records", parentGlobalList.size());
    }
}
