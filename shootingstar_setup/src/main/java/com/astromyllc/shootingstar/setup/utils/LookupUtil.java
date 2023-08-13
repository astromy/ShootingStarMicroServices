package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.model.Lookup;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.LookUpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LookupUtil {
    private final LookUpRepository lookUpRepository;
    public static List<Lookup> lookupGlobalList=null;

    @Bean
    private void findAllLookups(){
        lookupGlobalList=lookUpRepository.findAll();
        log.info("Global list of Lookups populated with {} records",lookupGlobalList.stream().count());
    }
}
