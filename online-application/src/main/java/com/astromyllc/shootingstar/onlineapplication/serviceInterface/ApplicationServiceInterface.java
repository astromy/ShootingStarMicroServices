package com.astromyllc.shootingstar.onlineapplication.serviceInterface;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicationRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.alien.AdmissionRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.response.ApplicationsResponse;
import com.astromyllc.shootingstar.onlineapplication.dto.response.alien.ProcessedApplicationResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public interface ApplicationServiceInterface {
    public ApplicationsResponse createApplication(ApplicationRequest applicationRequest) throws IOException, URISyntaxException;
    public void createApplicationList(ArrayList<ApplicationRequest> applicationRequests);
    public void UpdateApplicationList(ArrayList<ApplicationRequest> requestArrayList);
    public Optional<List<ApplicationsResponse>> getAllApplications();
    public Optional<ApplicationsResponse> getApplicationByApplicationCode(String applictionCode);
    public Optional<ApplicationsResponse> getApplicationById(String applicationId);
    public Optional<List<ApplicationsResponse>> getApplicationsBySchool(String schoolCode);
    public List<ApplicationsResponse> getApplicationsByDate(LocalDate applicationDate);
    public Optional<List<ApplicationsResponse>> getApplicationsByCountry(String Country);
    public Optional<List<ApplicationsResponse>> getApplicationsByCity(String City);
    public Optional<List<ApplicationsResponse>> getApplicationsByRegion(String Region);
    public Optional<List<ProcessedApplicationResponse>> getProcessedApplicationsBySchool(AdmissionRequest admissionRequest);
}
