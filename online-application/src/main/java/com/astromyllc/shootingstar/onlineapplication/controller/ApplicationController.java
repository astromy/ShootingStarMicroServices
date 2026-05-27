package com.astromyllc.shootingstar.onlineapplication.controller;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicationRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.alien.AdmissionRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.response.ApplicationsResponse;
import com.astromyllc.shootingstar.onlineapplication.dto.response.alien.ProcessedApplicationResponse;
import com.astromyllc.shootingstar.onlineapplication.serviceInterface.ApplicationServiceInterface;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
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
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {


    private final ApplicationServiceInterface applicationService;

    @PostMapping("/api/applications/submit-application")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack0")
    /*
    * If TimerLimiter is Used, It must return a CompletableFuture for both method and fallback
    * */
    @TimeLimiter(name = "application")
    @Retry(name = "application")
    public CompletableFuture<ApplicationsResponse> SubmitApplication(@RequestBody ApplicationRequest applicationRequest) throws IOException, URISyntaxException {
        log.info("Application  Received");
       return CompletableFuture.supplyAsync(()-> {
           try {
               return applicationService.createApplication(applicationRequest);
           } catch (IOException e) {
               throw new RuntimeException(e);
           } catch (URISyntaxException e) {
               throw new RuntimeException(e);
           }
       });
    }

    @PostMapping("/api/applications/submit-applicationList")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack1")
    public void SubmitApplicationList(@RequestBody ArrayList<ApplicationRequest> applicationRequest) {
        log.info("Application  Received");
        applicationService.createApplicationList(applicationRequest);
    }

    @PostMapping("/api/applications/updateApplicationList")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack2")
    public void updateAapplicationList(@RequestBody ArrayList<ApplicationRequest> applicationRequest) {
        applicationService.UpdateApplicationList(applicationRequest);
    }


    @PostMapping("/api/applications/getApplicationByCode")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack3")
    public Optional<ApplicationsResponse> getApplicationByCode(@RequestBody String code) {
        return applicationService.getApplicationByApplicationCode(code);
    }

    @PostMapping("/api/applications/getApplicationById")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack3")
    public Optional<ApplicationsResponse> getApplicationById(@RequestBody String applicationId) {
        return applicationService.getApplicationById(applicationId);
    }


    @PostMapping("/api/applications/getApplicationsBySchool")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack3")
    public Optional<List<ApplicationsResponse>> getApplicationsBySchool(@RequestBody String schoolId) {
        return applicationService.getApplicationsBySchool(schoolId);
    }


    @PostMapping("/api/applications/getProcessedApplicationsBySchool")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack3")
    public Optional<List<ProcessedApplicationResponse>> getProcessedApplicationsBySchool(@RequestBody AdmissionRequest admissionRequest) {
        return applicationService.getProcessedApplicationsBySchool(admissionRequest);
    }

    @PostMapping("/api/applications/getApplicationsByApplicationDate")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack3")
    public List<ApplicationsResponse> getApplicationsByDate(@RequestBody String date1) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
        LocalDate date = LocalDate.parse(date1,formatter);
        return applicationService.getApplicationsByDate(date);
    }


    @PostMapping("/api/applications/getApplicationsByCountry")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack3")
    public Optional<List<ApplicationsResponse>> getApplicationsByCountry(@RequestBody String country) {
        return applicationService.getApplicationsByCountry(country);
    }

    @PostMapping("/api/applications/getApplicationsByCity")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack3")
    public Optional<List<ApplicationsResponse>> getApplicationsByCity(@RequestBody String city) {
        return applicationService.getApplicationsByCity(city);
    }

    @PostMapping("/api/applications/getApplicationsByRegion")
    @ResponseStatus(HttpStatus.OK)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack3")
    public Optional<List<ApplicationsResponse>> getApplicationsByRegion(@RequestBody String region) {
        return applicationService.getApplicationsByRegion(region);
    }

    @PostMapping("/api/applications/getAllApplication")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @CircuitBreaker(name = "application",fallbackMethod = "fallBack4")
    public Optional<List<ApplicationsResponse>> getAllApplications() {
        return applicationService.getAllApplications();
    }


    public CompletableFuture<String> fallBack0(ApplicationRequest applicationRequest,RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(()-> "Temporal Failure, Try again after sometime");
    }
    public String fallBack1(ArrayList<ApplicationRequest> applicationRequest,RuntimeException runtimeException){
        return "Temporal Failure, Try again after sometime";
    }
    public String fallBack2(ArrayList<ApplicationRequest> applicationRequest,RuntimeException runtimeException){
        return "Temporal Failure, Try again after sometime";
    }
    public String fallBack3(String code,RuntimeException runtimeException){
        return "Temporal Failure, Try again after sometime";
    }
    public String fallBack4(RuntimeException runtimeException){
        return "Temporal Failure, Try again after sometime";
    }

}
