package com.astromyllc.shootingstar.accommodation.controller;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.accommodation.serviceInterface.BlockServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccommodationController {
    private final BlockServiceInterface blockServiceInterface;

    @PostMapping("/api/accommodation/addInstitutionAccommodation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity addInstitutionAccommodation(@RequestBody BlockRequest blockRequest) {
        log.info("Block Received");
        return ResponseEntity.ok(blockServiceInterface.addInstitutionAccommodation(blockRequest));
    }

    @PostMapping("/api/accommodation/getInstitutionAccommodation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity getInstitutionAccommodation(@RequestBody SingleStringRequest request) {
        log.info("Block Received");
        return ResponseEntity.ok(blockServiceInterface.getInstitutionAccommodation(request));
    }

}
