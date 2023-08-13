package com.astromyllc.shootingstar.setup.controller;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionResponse;
import com.astromyllc.shootingstar.setup.service.InstitutionService;
import com.astromyllc.shootingstar.setup.serviceInterface.InstitutionServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InstitutionController {
    private final InstitutionServiceInterface institutionService;

    @PostMapping
    @RequestMapping("/api/setup/signup_institution")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitApplication(@RequestBody InstitutionRequest institutionRequest) throws IOException, URISyntaxException {
        log.info("Application  Received");
        institutionService.createInstitution(institutionRequest);
    }

    @PostMapping
    @RequestMapping("/api/setup/getAllinstitution")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<InstitutionResponse>> getAllInstitution() throws IOException, URISyntaxException {
        log.info("Application  Received");
      return institutionService.getAllInstitution();
    }

    @PostMapping
    @RequestMapping("/api/setup/getInstitutionByCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCode(@RequestBody String beceCode) throws IOException, URISyntaxException {
        log.info("Application  Received");
        return institutionService.getInstitutionByBeceCode(beceCode);
    }
    @GetMapping
    @RequestMapping("/api/setup/getInstitutionByCode?institutionCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCodePath(@PathVariable ("institutionCode") String beceCode) throws IOException, URISyntaxException {
        log.info("Application  Received");
        return institutionService.getInstitutionByBeceCode(beceCode);
    }
}
