package com.astromyllc.shootingstar.onlineapplication.serviceInterface;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicantStudentSkimRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.DynamicStringRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.RefundRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.Students2Request;
import com.astromyllc.shootingstar.onlineapplication.dto.request.alien.AdmissionRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.response.ApplicationsResponse;
import com.astromyllc.shootingstar.onlineapplication.dto.response.alien.ProcessedApplicationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public interface ApplicationServiceInterface {
    public ApplicationsResponse createApplication(Students2Request applicationRequest) throws IOException, URISyntaxException;

    public void createApplicationList(ArrayList<Students2Request> applicationRequests);

    public void UpdateApplicationList(ArrayList<ApplicantStudentSkimRequest> requestArrayList);

    public Optional<List<ApplicationsResponse>> getAllApplications();

    public Optional<ApplicationsResponse> getApplicationByApplicationCode(DynamicStringRequest applictionCode);

    public Optional<ApplicationsResponse> getApplicationById(String applicationId);

    public Optional<List<ApplicationsResponse>> getApplicationsBySchool(String schoolCode);

    public List<ApplicationsResponse> getApplicationsByDate(LocalDate applicationDate);

    public Optional<List<ApplicationsResponse>> getApplicationsByCountry(String Country);

    public Optional<List<ApplicationsResponse>> getApplicationsByCity(String City);

    public Optional<List<ApplicationsResponse>> getApplicationsByRegion(String Region);

    public Optional<List<ProcessedApplicationResponse>> getProcessedApplicationsBySchool(AdmissionRequest admissionRequest);

    public Mono<ResponseEntity<String>> refundPayment(RefundRequest refundRequest);

}
