package com.astromyllc.shootingstar.onlineapplication.service;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicationRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.alien.AdmissionRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.response.ApplicationsResponse;
import com.astromyllc.shootingstar.onlineapplication.dto.response.alien.ProcessedApplicationResponse;
import com.astromyllc.shootingstar.onlineapplication.model.Applications;
import com.astromyllc.shootingstar.onlineapplication.repository.ApplicationsRepository;
import com.astromyllc.shootingstar.onlineapplication.serviceInterface.ApplicationServiceInterface;
import com.astromyllc.shootingstar.onlineapplication.utils.ApplicationUtilities;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationService implements ApplicationServiceInterface {


    private final ApplicationsRepository applicationsRepository;
    private final ApplicationUtilities util;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ApplicationsResponse createApplication(ApplicationRequest applicationRequest) throws IOException, URISyntaxException {
        Applications app = util.mapApplicationRequest_ToApplications(applicationRequest);
        /* Adds to The Bean List with the current new record*/
        if (app != null){
        applicationsRepository.save(app);
        util.apl.add(app);
        log.info("Application {} Saved Successfully", app.getIdapplication());
    }else{
            log.info("Application not saved");
        }
        return util.mapApplications_ToApplicationResponse(app);
    }


    public void createApplicationList(ArrayList<ApplicationRequest> applicationRequests) {

        ArrayList<Applications> applicationList = (ArrayList<Applications>) applicationRequests.stream()
                .map(app-> {
                    try {
                        return util.mapApplicationRequest_ToApplications(app);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        /* Adds to The Bean List with the current new records*/
        applicationsRepository.saveAll(applicationList);
        util.apl.addAll(applicationList);
        log.info(" {} Application Saved Successfully", applicationList.stream().count());
    }



    public void UpdateApplicationList(ArrayList<ApplicationRequest> requestArrayList) {
        List<Applications> applications= applicationsRepository
                .findAll()
                .stream()
                .filter(a->requestArrayList.stream()
                        .anyMatch(request->a.getIdapplication().equalsIgnoreCase(request.getIdapplication()))).toList();
        applications.stream().map(applications1->util.mapApplications_ToApplicationResponse(applications1)).toList();

        applicationsRepository.saveAll(applications);
        util.apl.addAll(applications);
        log.info(" {} Application Saved Successfully", applications.stream().count());
    }

    public Optional<List<ApplicationsResponse>> getAllApplications() {
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().map(applications1 -> util.mapApplications_ToApplicationResponse(applications1)).toList())
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application Found"))));
    }

    @Override
    public Optional<ApplicationsResponse> getApplicationByApplicationCode(String applictionCode) {
        String finalApplictionCode = applictionCode.split("\"")[3];

       return Optional.ofNullable(util.apl.stream().filter(x -> x.getApplicationCode().equalsIgnoreCase(finalApplictionCode)).findFirst().map(x->util.mapApplications_ToApplicationResponse(x)))
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Code %s", finalApplictionCode)));
    }

    @Override
    public Optional<ApplicationsResponse> getApplicationById(String applicationId) {
        String finalApplictionId = applicationId.split("\"")[3];
        return Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication().equalsIgnoreCase(finalApplictionId)).findFirst().map(x->util.mapApplications_ToApplicationResponse(x)))
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Code %s", finalApplictionId)));
    }

    @Override
    public Optional<List<ApplicationsResponse>> getApplicationsBySchool(String schoolId) {
        String finalschoolId = schoolId.split("\"")[3];//.split("\"")[1];
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().filter(x -> x.getApplicationInstitution().equalsIgnoreCase(finalschoolId)).toList().stream().map(x->util.mapApplications_ToApplicationResponse(x)).toList())
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application School %s", finalschoolId))));
    }

    @Override
    public List<ApplicationsResponse> getApplicationsByDate(LocalDate applicationDate) {
        return null;
    }

    @Override
    public Optional<List<ApplicationsResponse>> getApplicationsByCountry(String Country) {
        String finalCountry = Country.split("\"")[3];
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication().equalsIgnoreCase(finalCountry)).toList().stream().map(x->util.mapApplications_ToApplicationResponse(x)).toList())
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Code %s", finalCountry))));

    }

    @Override
    public Optional<List<ApplicationsResponse>> getApplicationsByCity(String City) {
        String finalCity = City.split("\"")[3];
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication().equalsIgnoreCase(finalCity)).toList().stream().map(x->util.mapApplications_ToApplicationResponse(x)).toList())
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Code %s", finalCity))));
    }

    @Override
    public Optional<List<ApplicationsResponse>> getApplicationsByRegion(String Region) {
        String finalRegion = Region.split("\"")[3];
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication().equalsIgnoreCase(finalRegion)).toList().stream().map(x->util.mapApplications_ToApplicationResponse(x)).toList())
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Region %s", finalRegion))));
    }

    @Override
    public Optional<List<ProcessedApplicationResponse>> getProcessedApplicationsBySchool(AdmissionRequest admissionRequest) {
        return Optional.ofNullable(Optional.of(util.apl.stream().filter(x -> x.getApplicationInstitution().equalsIgnoreCase(admissionRequest.getInstitutionCode())
                                && x.getApplicationStatus().equalsIgnoreCase(admissionRequest.getApplicationStatus())
                                && x.getApplicationDate().getYear()== LocalDateTime.parse(admissionRequest.getApplicationDate(),formatter).getYear()).toList()
                        .stream().map(x->util.mapApplications_ToProcessedApplicationResponse(x)).toList())
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application School %s", admissionRequest.getInstitutionCode()))));

    }
}
