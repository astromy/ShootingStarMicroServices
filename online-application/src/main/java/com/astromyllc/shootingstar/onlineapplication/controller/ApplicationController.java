package com.astromyllc.shootingstar.onlineapplication.controller;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicationRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.response.ApplicationsResponse;
import com.astromyllc.shootingstar.onlineapplication.serviceInterface.ApplicationServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {


    private final ApplicationServiceInterface applicationService;

    @PostMapping
    @RequestMapping("/api/applications/submit-application")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationsResponse SubmitApplication(@RequestBody ApplicationRequest applicationRequest) throws IOException, URISyntaxException {
        log.info("Application  Received");
       return applicationService.createApplication(applicationRequest);
    }

    @PostMapping
    @RequestMapping("/api/applications/submit-applicationList")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitApplicationList(@RequestBody ArrayList<ApplicationRequest> applicationRequest) {
        log.info("Application  Received");
        applicationService.createApplicationList(applicationRequest);
    }

    @PostMapping
    @RequestMapping("/api/applications/updateAapplicationList")
    @ResponseStatus(HttpStatus.OK)
    public void updateAapplicationList(@RequestBody ArrayList<ApplicationRequest> applicationRequest) {
        applicationService.UpdateApplicationList(applicationRequest);
    }

    @PostMapping
    @RequestMapping("/api/applications/getApplicationByCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ApplicationsResponse> getApplicationByCode(@RequestBody String code) {
        return applicationService.getApplicationByApplicationCode(code);
    }

    @PostMapping
    @RequestMapping("/api/applications/getApplicationById")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ApplicationsResponse> getApplicationById(@RequestBody String applicationId) {
        return applicationService.getApplicationById(applicationId);
    }


    @PostMapping
    @RequestMapping("/api/applications/getApplicationsBySchool")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<ApplicationsResponse>> getApplicationsBySchool(@RequestBody Long schoolId) {
        return applicationService.getApplicationsBySchool(schoolId);
    }

    @PostMapping
    @RequestMapping("/api/applications/getAApplicationsByApplicationDate")
    @ResponseStatus(HttpStatus.OK)
    public List<ApplicationsResponse> getApplicationsByDate(@RequestBody String date1) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
        LocalDate date = LocalDate.parse(date1,formatter);
        return applicationService.getApplicationsByDate(date);
    }


    @PostMapping
    @RequestMapping("/api/applications/getAApplicationsByCountry")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<ApplicationsResponse>> getApplicationsByCountry(@RequestBody String country) {
        return applicationService.getApplicationsByCountry(country);
    }

    @PostMapping
    @RequestMapping("/api/applications/getAApplicationsByCity")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<ApplicationsResponse>> getApplicationsByCity(@RequestBody String city) {
        return applicationService.getApplicationsByCity(city);
    }

    @PostMapping
    @RequestMapping("/api/applications/getApplicationsByRegion")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<ApplicationsResponse>> getApplicationsByRegion(@RequestBody String region) {
        return applicationService.getApplicationsByRegion(region);
    }

    @PostMapping
    @RequestMapping("/api/applications/getAllAapplication")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<ApplicationsResponse>> getAllApplications() {
        return applicationService.getAllApplications();
    }

}
