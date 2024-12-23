package com.astromyllc.shootingstar.hr.controller;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffPermissionsRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.api.StaffCodeRequest;
import com.astromyllc.shootingstar.hr.dto.response.StaffResponse;
import com.astromyllc.shootingstar.hr.serviceInterface.StaffServiceInterface;
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
public class StaffController {


    private final StaffServiceInterface staffServiceInterface;

    @PostMapping
    @RequestMapping("/api/hr/createStaff")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<StaffResponse> SubmitStaff(@RequestBody List<StaffRequest> staffRequest) throws IOException, URISyntaxException {
        log.info("hr Received");
        return staffServiceInterface.createStaff(staffRequest);
    }

    @PostMapping
    @RequestMapping("/api/hr/addStaffPermissions")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<StaffResponse> AddStaffPermissions(@RequestBody List<StaffPermissionsRequest> staffPermissionsRequests) throws IOException, URISyntaxException {
        log.info("hr received {} records of StaffPermissions",staffPermissionsRequests);
        return staffServiceInterface.addStaffPermissions(staffPermissionsRequests);
    }

    @PostMapping
    @RequestMapping("/api/hr/getStaffByInstitutionAndCode")
    @ResponseStatus(HttpStatus.FOUND)
    public Optional<StaffResponse> getStaffByIntitutionAndStaffCode(@RequestBody StaffCodeRequest staffcodeRequest) throws IOException, URISyntaxException {
        log.info("Fetching Staff Based on Institution and Staff Code");
        return staffServiceInterface.getStaffByCode(staffcodeRequest);
    }

    @PostMapping
    @RequestMapping("/api/hr/getStaffByCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<StaffResponse>> getStaffByInstitution(@RequestBody SingleStringRequest staffcodeRequest) throws IOException, URISyntaxException {
        log.info("Fetching List of Staff Based on Institution");
        return staffServiceInterface.getStaffByInstitution(staffcodeRequest);
    }
}
