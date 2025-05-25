package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.LookupRequest;
import com.astromyllc.shootingstar.setup.dto.response.LookupResponse;
import com.astromyllc.shootingstar.setup.model.Lookup;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.LookUpRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LookupUtil {
    private final LookUpRepository lookUpRepository;
    public static List<Lookup> lookupGlobalList = null;

    @PostConstruct
    private void findAllLookups() {
        lookupGlobalList = lookUpRepository.findAll();
        log.info("Global list of Lookups populated with {} records", lookupGlobalList.stream().count());
    }

    public Optional<LookupResponse> mapLookUp_ToLookUpResponse(Lookup l) {
        return Optional.ofNullable(LookupResponse.builder()
                .id(l.getIdLookup())
                .name(l.getName())
                .type(l.getType())
                .build());
    }

    public Lookup mapLookupRequest_ToLookup(LookupRequest l) {
        return Lookup.builder()
                .idLookup(l.getId())
                .type(l.getType())
                .name(l.getName())
                .build();
    }

}
