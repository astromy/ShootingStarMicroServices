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
import java.util.stream.Collectors;

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
        List<Lookup> lu=lookupRequestList.stream().map(l->lookupUtil.mapLookupRequest_ToLookup(l)).collect(Collectors.toList());
        lookUpRepository.saveAll(lu);
        lookupUtil.lookupGlobalList.addAll(lu);
        return lu.stream().map(u->lookupUtil.mapLookUp_ToLookUpResponse(u)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<LookupResponse>> getAllLookups() {
        return lookupUtil.lookupGlobalList.stream().map(l->lookupUtil.mapLookUp_ToLookUpResponse(l)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<LookupResponse>> getAllLookupsByType(SingleStringRequest lookupType1) {
        String lookupType= lookupType1.getVal();
        return lookupUtil.lookupGlobalList.stream().filter(x->x.getType().equals(lookupType)).map(y->lookupUtil.mapLookUp_ToLookUpResponse(y)).collect(Collectors.toList());
    }
}
