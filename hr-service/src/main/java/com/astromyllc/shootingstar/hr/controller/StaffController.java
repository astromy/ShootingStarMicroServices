package com.astromyllc.shootingstar.hr.controller;

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

@RestController
@RequiredArgsConstructor
@Slf4j
public class StaffController {


    private final StaffServiceInterface staffServiceInterface;

    @PostMapping
    @RequestMapping("/api/hr/createStaff")
    @ResponseStatus(HttpStatus.CREATED)
    public StaffResponse SubmitStaff(@RequestBody StaffRequest staffRequest) throws IOException, URISyntaxException {
        log.info("hr Received");
        return staffServiceInterface.createStaff(staffRequest);
    }

    @PostMapping
    @RequestMapping("/api/hr/getStaffByCode")
    @ResponseStatus(HttpStatus.CREATED)
    public StaffResponse getStaffByIntitutionAndStaffCode(@RequestBody StaffCodeRequest staffcodeRequest) throws IOException, URISyntaxException {
        log.info("Application  Received");
        return staffServiceInterface.getStaffByCode(staffcodeRequest);
    }
}
