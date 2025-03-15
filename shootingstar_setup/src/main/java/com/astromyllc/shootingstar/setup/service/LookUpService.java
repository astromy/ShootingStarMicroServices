package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.LookupRequest;
import com.astromyllc.shootingstar.setup.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.setup.dto.response.LookupResponse;
import com.astromyllc.shootingstar.setup.model.Lookup;
import com.astromyllc.shootingstar.setup.repository.LookUpRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.LookupServiceInterface;
import com.astromyllc.shootingstar.setup.utils.LookupUtil;
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
    private final LookupUtil lookupUtil;
    private final LookUpRepository lookUpRepository;
    @Override
    public void createLookup(LookupRequest lookupRequest) {

    }

    @Override
    public List<Optional<LookupResponse>> createLookups(List<LookupRequest> lookupRequestList) {

        List<Lookup> lu = lookupRequestList.stream()
                .filter(c -> LookupUtil.lookupGlobalList.stream()
                        .noneMatch(d -> c.getName().equalsIgnoreCase(d.getName()) && c.getType().equalsIgnoreCase(d.getType()))) // Filter out matching Lookup
                .map(lookupUtil::mapLookupRequest_ToLookup) // Directly map remaining Lookups
                .toList();

        if (lu.size() > 0) {
            lookUpRepository.saveAll(lu);
            LookupUtil.lookupGlobalList.addAll(lu);
        }
            return lu.stream().map(lookupUtil::mapLookUp_ToLookUpResponse).toList();

    }

    @Override
    public List<Optional<LookupResponse>> getAllLookups() {
        return LookupUtil.lookupGlobalList.stream().map(lookupUtil::mapLookUp_ToLookUpResponse).toList();
    }

    @Override
    public List<Optional<LookupResponse>> getAllLookupsByType(SingleStringRequest lookupType1) {
        String lookupType= lookupType1.getVal();
        return LookupUtil.lookupGlobalList.stream().filter(x->x.getType().equalsIgnoreCase(lookupType)).map(lookupUtil::mapLookUp_ToLookUpResponse).toList();
    }

    @Override
    public Optional<Optional<LookupResponse>> getLookUpById(String id) {

        return Optional.of(LookupUtil.lookupGlobalList.stream()
                .filter(x->x.getIdLookup().toString().equalsIgnoreCase(id))
                .findFirst().get())
                .map(lookupUtil::mapLookUp_ToLookUpResponse);
    }
}
