package com.astromyllc.shootingstar.setup.serviceInterface;

import com.astromyllc.shootingstar.setup.dto.request.LookupRequest;
import com.astromyllc.shootingstar.setup.dto.response.LookupResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface LookupServiceInterface {
    public void createLookup(LookupRequest lookupRequest);
    public  List<Optional<LookupResponse>> createLookups(List<LookupRequest> lookupRequestList);
    public List<Optional<LookupResponse>> getAllLookups();
    public List<Optional<LookupResponse>> getAllLookupsByType(String lookupType);
}
