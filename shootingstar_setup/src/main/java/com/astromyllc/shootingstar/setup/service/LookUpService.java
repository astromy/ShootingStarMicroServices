package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.LookupRequest;
import com.astromyllc.shootingstar.setup.dto.response.LookupResponse;
import com.astromyllc.shootingstar.setup.serviceInterface.LookupServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LookUpService implements LookupServiceInterface {
    @Override
    public void createLookup(LookupRequest lookupRequest) {

    }

    @Override
    public void createLookups(List<LookupRequest> lookupRequestList) {

    }

    @Override
    public Optional<List<LookupResponse>> getAllLookups() {
        return Optional.empty();
    }

    @Override
    public Optional<List<LookupResponse>> getAllLookupsByType(String lookupType) {
        return Optional.empty();
    }
}
