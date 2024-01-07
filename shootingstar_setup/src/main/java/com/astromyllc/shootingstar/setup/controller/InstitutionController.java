package com.astromyllc.shootingstar.setup.controller;

import com.astromyllc.shootingstar.setup.dto.request.*;
import com.astromyllc.shootingstar.setup.dto.response.*;
import com.astromyllc.shootingstar.setup.service.InstitutionService;
import com.astromyllc.shootingstar.setup.serviceInterface.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    private final LookupServiceInterface lookupServiceInterface;
    private final DepartmentServiceInterface departmentServiceInterface;
    private final ClassesServiceInterface classesServiceInterface;
    private final SubjectServiceInterface subjectServiceInterface;
    private final GradingSettingsServiceInterface gradingSettingsServiceInterface;
    private final DesignationServiceInterface designationServiceInterface;
    private final JobDescriptionServiceInterface jobDescriptionServiceInterface;

//=============================== INSTITUTION ========================================================
    /**
     *
     * @param institutionRequest
     */
    @PostMapping("/api/setup/signupInstitution")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "Institution",fallbackMethod = "fallBack0")
    public void SubmitApplication(@RequestBody InstitutionRequest institutionRequest) {
        log.info("Application  Received");
        institutionService.createInstitution(institutionRequest);
    }

    @PostMapping("/api/setup/getAllinstitution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<InstitutionResponse>> getAllInstitution() {
        log.info("Application  Received");
        return institutionService.getAllInstitution();
    }

    @PostMapping("/api/setup/getInstitutionByCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCode(@RequestBody SingleStringRequest beceCode) {
        log.info("Application  Received");
        return institutionService.getInstitutionByBeceCode(beceCode);
    }

    @GetMapping("/api/setup/getInstitutionByCode?institutionCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCodePath(@PathVariable ("institutionCode") SingleStringRequest beceCode) {
        log.info("Application  Received");
        return institutionService.getInstitutionByBeceCode(beceCode);
    }




 //========================== PRE-REQUEST ===============================================
    @PostMapping("/api/setup/preRequestInstitution")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitPreOrderApplication(@RequestBody PreOrderInstitutionRequest institutionRequest) {
        log.info("Application  Received");
        institutionService.createPreOrderInstitution(institutionRequest);
    }

    @PostMapping("/api/setup/getPreOrderAllinstitution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<PreOrderInstitutionResponse>> getAllPreOrderedInstitution() {
        log.info("Application  Received");
        return institutionService.getAllPreOrderedInstitution();
    }



 //================================== DEPARTMENTS ====================================================

    @PostMapping("/api/setup/addDepartment")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitPreOrderApplication(@RequestBody DepartmentRequest departmentRequest) {
        log.info("Application  Received");
        departmentServiceInterface.createDepartments(departmentRequest);
    }

    @PostMapping("/api/setup/getInstitutionDepartment")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<DepartmentResponse>> getDepartmentByInstitution(@RequestBody SingleStringRequest beceCode) {
        log.info("Application  Received");
      return  departmentServiceInterface.getDepartmentByInstitution(beceCode);
    }




//================================= LOOKUPS ============================================================================

    @PostMapping("/api/setup/addLookUp")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitLookup(@RequestBody LookupRequest lookupRequest) {
        log.info("Application  Received");
        lookupServiceInterface.createLookup(lookupRequest);
    }
    @PostMapping("/api/setup/addLookUps")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitLookupList(@RequestBody List<LookupRequest> lookupRequest) {
        log.info("Application  Received");
        lookupServiceInterface.createLookups(lookupRequest);
    }

    @PostMapping("/api/setup/getLookUpByType")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<LookupResponse>> getLookUpType(@RequestBody SingleStringRequest lookupType) {
        log.info("Request  Received");
       return lookupServiceInterface.getAllLookupsByType(lookupType);
    }

//================================== CLASSES ===========================================================================

    @PostMapping("/api/setup/addClasses")
    @ResponseStatus(HttpStatus.CREATED)
    public void AddClasses(@RequestBody ClassesRequest classesRequest) {
        log.info("Class List  Received");
        classesServiceInterface.createClasses(classesRequest);
    }

    @PostMapping("/api/setup/getInstitutionClasses")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<Optional<ClassesResponse>>> getClasses(@RequestBody SingleStringRequest beceCode) {
        log.info("Class List  Received");
       return classesServiceInterface.getAllClassesByInstitution(beceCode);
    }



//================================== SUBJECTS ==========================================================================

    @PostMapping("/api/setup/addSubjects")
    @ResponseStatus(HttpStatus.CREATED)
    public void AddSubjects(@RequestBody List<SubjectRequest> subjectRequest) {
        log.info("Subject List  Received");
        subjectServiceInterface.createSubjects(subjectRequest);
    }

    @PostMapping("/api/setup/getInstitutionSubjects")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<SubjectResponse>> GetSubjects(@RequestBody SingleStringRequest beceCode) {
        log.info("Subject List  Received");
       return subjectServiceInterface.getAllSubjectsByInstitution(beceCode);
    }



//================================== GRADING SETTING ===================================================================


    @PostMapping("/api/setup/addGradingSetting")
    @ResponseStatus(HttpStatus.CREATED)
    public void AddGradingSetting(@RequestBody GradingSettingRequest gradingSettingRequests) {
        log.info("Grade Setting  Received");
        gradingSettingsServiceInterface.createGradingSetting(gradingSettingRequests);
    }

    @PostMapping("/api/setup/getInstitutionGradingSetting")
    @ResponseStatus(HttpStatus.OK)
    public Optional<GradingSettingResponse> GetGradingSettingByCode(@RequestBody SingleStringRequest beceCode) {
        log.info("Grade Setting  Received");
       return gradingSettingsServiceInterface.getAllGradingSettingsByInstitution(beceCode);
    }


//====================================== DESIGNATION ===================================================================


    @PostMapping("/api/setup/addDesignations")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<DesignationResponse>> AddDesignation(@RequestBody DesignationRequest designationRequest) {
        log.info("Designation  Received");
      return  designationServiceInterface.createDesignation(designationRequest);
    }

    @PostMapping("/api/setup/getInstitutionDesignations")
    @ResponseStatus(HttpStatus.OK)
    public  List<List<Optional<DesignationResponse>>> GetDesignationByInstitution(@RequestBody DesignationRequest designationRequest) {
        log.info("Designation Request  Received");
        return designationServiceInterface.getAllDesignationByInstitution(designationRequest);
    }



//======================================== JOB DESCRIPTION =============================================================

    @PostMapping("/api/setup/addJobDescription")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<JobDescriptionResponse> AddJobDescription(@RequestBody JobDescriptionRequest jobDescriptionRequest) {
        log.info("Job Description  Received");
      return  jobDescriptionServiceInterface.createJobDescriptions(jobDescriptionRequest);
    }

    @PostMapping("/api/setup/getInstitutionJobDescriptions")
    @ResponseStatus(HttpStatus.OK)
    public List<List<List<Optional<JobDescriptionResponse>>>> getInstitutionJobDescriptions(@RequestBody SingleStringRequest beceCode) {
        log.info("Job Description Request  Received");
        return jobDescriptionServiceInterface.getAllJobDescriptionsByInstitution(beceCode);
    }



//______________________________________________________________________________________________________________________

    public String fallBack0(InstitutionRequest institutionRequest,RuntimeException runtimeException){
        return "Temporal Failure, Try again after sometime";
    }
}
