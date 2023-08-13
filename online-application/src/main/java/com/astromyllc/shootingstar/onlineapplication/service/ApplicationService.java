package com.astromyllc.shootingstar.onlineapplication.service;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicationRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.response.ApplicationsResponse;
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
                .collect(Collectors.toList());
        /* Adds to The Bean List with the current new records*/
        applicationsRepository.insert(applicationList);
        util.apl.addAll(applicationList);
        log.info(" {} Application Saved Successfully", applicationList.stream().count());
    }



    public void UpdateApplicationList(ArrayList<ApplicationRequest> requestArrayList) {
        List<Applications> applications= applicationsRepository
                .findAll()
                .stream()
                .filter(a->requestArrayList.stream()
                        .anyMatch(request->a.getIdapplication().equals(request.getIdapplication()))).toList();
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
        String finalApplictionCode = applictionCode.split("\"")[3];//.split("\"")[1];util.applicationsList().stream().filter(x->x.getApplicationCode()==finalApplictionCode).findFirst())
        return Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication() == finalApplictionCode).findFirst().map(x->util.mapApplications_ToApplicationResponse(x)))
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Code %s", finalApplictionCode)));
    }

    @Override
    public Optional<ApplicationsResponse> getApplicationById(String applicationId) {
        String finalApplictionId = applicationId.split("\"")[3];//.split("\"")[1];util.applicationsList().stream().filter(x->x.getIdapplication()==finalApplictionId).findFirst()
        return Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication() == finalApplictionId).findFirst().map(x->util.mapApplications_ToApplicationResponse(x)))
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Code %s", finalApplictionId)));
    }

    @Override
    public Optional<List<ApplicationsResponse>> getApplicationsBySchool(Long schoolId) {
        String finalschoolId = schoolId.toString().split("\"")[3];//.split("\"")[1];
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication() == finalschoolId).toList().stream().map(x->util.mapApplications_ToApplicationResponse(x)).toList())
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application School %s", finalschoolId))));
    }

    @Override
    public List<ApplicationsResponse> getApplicationsByDate(LocalDate applicationDate) {
        return null;
    }

    @Override
    public Optional<List<ApplicationsResponse>> getApplicationsByCountry(String Country) {
        String finalCountry = Country.split("\"")[3];
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication() == finalCountry).collect(Collectors.toList()).stream().map(x->util.mapApplications_ToApplicationResponse(x)).collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Code %s", finalCountry))));

    }

    @Override
    public Optional<List<ApplicationsResponse>> getApplicationsByCity(String City) {
        String finalCity = City.split("\"")[3];
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication() == finalCity).collect(Collectors.toList()).stream().map(x->util.mapApplications_ToApplicationResponse(x)).collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Code %s", finalCity))));
    }

    @Override
    public Optional<List<ApplicationsResponse>> getApplicationsByRegion(String Region) {
        String finalRegion = Region.split("\"")[3];
        return Optional.ofNullable(Optional.ofNullable(util.apl.stream().filter(x -> x.getIdapplication() == finalRegion).collect(Collectors.toList()).stream().map(x->util.mapApplications_ToApplicationResponse(x)).collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException(String.format("No Record of Application with Application Region %s", finalRegion))));
    }
}
