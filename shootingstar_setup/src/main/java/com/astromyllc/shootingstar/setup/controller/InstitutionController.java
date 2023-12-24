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

    @RequestMapping("/api/setup/getAllinstitution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<InstitutionResponse>> getAllInstitution() {
        log.info("Application  Received");
        return institutionService.getAllInstitution();
    }

    @PostMapping("/api/setup/getInstitutionByCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCode(@RequestBody String beceCode) {
        log.info("Application  Received");
        return institutionService.getInstitutionByBeceCode(beceCode);
    }

    @GetMapping("/api/setup/getInstitutionByCode?institutionCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCodePath(@PathVariable ("institutionCode") String beceCode) {
        log.info("Application  Received");
        return institutionService.getInstitutionByBeceCode(beceCode);
    }




 //========================== PRE-REQUEST ===============================================
    @RequestMapping("/api/setup/preRequestInstitution")
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

    @RequestMapping("/api/setup/addDepartment")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitPreOrderApplication(@RequestBody List<DepartmentRequest> departmentRequest) {
        log.info("Application  Received");
        departmentServiceInterface.createDepartments(departmentRequest);
    }

    @RequestMapping("/api/setup/getInstitutionDepartment")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<DepartmentResponse>> getDepartmentByInstitution(@RequestBody String beceCode) {
        log.info("Application  Received");
      return  departmentServiceInterface.getDepartmentByInstitution(beceCode);
    }




//================================= LOOKUPS ============================================================

    @RequestMapping("/api/setup/addClassGroups")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitPreOrderApplication(@RequestBody LookupRequest lookupRequest) {
        log.info("Application  Received");
        lookupServiceInterface.createLookup(lookupRequest);
    }

    @RequestMapping("/api/setup/getLookUpByType")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<LookupResponse>> getLookUpType(@RequestBody String lookupType) {
        log.info("Request  Received");
       return lookupServiceInterface.getAllLookupsByType(lookupType);
    }

//================================== CLASSES =============================================================

    @RequestMapping("/api/setup/addClassess")
    @ResponseStatus(HttpStatus.CREATED)
    public void AddClassess(@RequestBody List<ClassesRequest> classesRequest) {
        log.info("Class List  Received");
        classesServiceInterface.createClasses(classesRequest);
    }

    @RequestMapping("/api/setup/getInstitutionClassess")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<ClassesResponse>> getClassess(@RequestBody String beceCode) {
        log.info("Class List  Received");
       return classesServiceInterface.getAllClassesByInstitution(beceCode);
    }



//================================== SUBJECTS ======================================================================

    @RequestMapping("/api/setup/addSubjects")
    @ResponseStatus(HttpStatus.CREATED)
    public void AddSubjects(@RequestBody List<SubjectRequest> subjectRequest) {
        log.info("Subject List  Received");
        subjectServiceInterface.createSubjects(subjectRequest);
    }

    @RequestMapping("/api/setup/getInstitutionSubjects")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<SubjectResponse>> GetSubjects(@RequestBody String beceCode) {
        log.info("Subject List  Received");
       return subjectServiceInterface.getAllSubjectsByInstitution(beceCode);
    }



//================================== GRADING SETTING ================================================================


    @RequestMapping("/api/setup/addGradingSetting")
    @ResponseStatus(HttpStatus.CREATED)
    public void AddGradingSetting(@RequestBody List<GradingSettingRequest> gradingSettingRequests) {
        log.info("Grade Setting  Received");
        gradingSettingsServiceInterface.createGradingSetting(gradingSettingRequests);
    }

    @RequestMapping("/api/setup/getInstitutionGradingSetting")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<GradingSettingResponse>> GetGradingSettingByCode(@RequestBody String beceCode) {
        log.info("Grade Setting  Received");
       return gradingSettingsServiceInterface.getAllGradingSettingsByInstitution(beceCode);
    }


    public String fallBack0(InstitutionRequest institutionRequest,RuntimeException runtimeException){
        return "Temporal Failure, Try again after sometime";
    }
}
