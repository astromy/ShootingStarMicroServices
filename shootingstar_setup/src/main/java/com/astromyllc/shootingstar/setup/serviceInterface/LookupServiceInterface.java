package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.LookupRequest;
import com.astromyllc.shootingstar.setup.dto.response.LookupResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface LookupServiceInterface {
    public void createLookup(LookupRequest lookupRequest);
    public  void createLookups(List<LookupRequest> lookupRequestList);
    public Optional<List<LookupResponse>> getAllLookups();
    public Optional<List<LookupResponse>> getAllLookupsByType(String lookupType);
}
