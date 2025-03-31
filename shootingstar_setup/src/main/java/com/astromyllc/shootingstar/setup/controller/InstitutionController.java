package com.astromyllc.shootingstar.setup.controller;

import com.astromyllc.shootingstar.setup.dto.request.*;
import com.astromyllc.shootingstar.setup.dto.response.*;
import com.astromyllc.shootingstar.setup.serviceInterface.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    private final AdmissionsServiceInterface admissionsServiceInterface;

//=============================== INSTITUTION ========================================================
    /**
     *
     * @param institutionRequest
     */
    @PostMapping("/api/setup/signupInstitution")
    @ResponseStatus(HttpStatus.CREATED)
    //@CircuitBreaker(name = "Institution",fallbackMethod = "fallBack0")
    public InstitutionResponse SubmitApplication(@RequestBody InstitutionRequest institutionRequest) {
        return institutionService.createInstitution(institutionRequest);
    }

    @PostMapping("/api/setup/getAllinstitution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<InstitutionResponse>> getAllInstitution() {
        return institutionService.getAllInstitution();
    }

    @PostMapping("/api/setup/getInstitutionByCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCode(@RequestBody SingleStringRequest beceCode) {
        return institutionService.getInstitutionByBeceCode(beceCode);
    }

    @GetMapping("/api/setup/getInstitutionByCode?institutionCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCodePath(@PathVariable ("institutionCode") SingleStringRequest beceCode) {
        return institutionService.getInstitutionByBeceCode(beceCode);
    }




 //========================== PRE-REQUEST ===============================================
    @PostMapping("/api/setup/preRequestInstitution")
    @ResponseStatus(HttpStatus.CREATED)
    public String SubmitPreOrderApplication(@RequestBody PreOrderInstitutionRequest institutionRequest) {
      return institutionService.createPreOrderInstitution(institutionRequest);
    }

    @PostMapping("/api/setup/getPreOrderAllinstitution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<PreOrderInstitutionResponse>> getAllPreOrderedInstitution() {
        return institutionService.getAllPreOrderedInstitution();
    }

    @PostMapping("/api/setup/migratePreOrder")
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionResponse migratePreOrder(@RequestBody SingleStringRequest beceCode) {
        return institutionService.migratePreOrder(beceCode.getVal());
    }



 //================================== DEPARTMENTS ====================================================

    @PostMapping("/api/setup/addDepartment")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<Optional<DepartmentResponse>>> AddDepartment(@RequestBody DepartmentRequest departmentRequest) {
       return departmentServiceInterface.createDepartments(departmentRequest);
    }

    @PostMapping("/api/setup/getInstitutionDepartment")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<DepartmentResponse>> getDepartmentByInstitution(@RequestBody SingleStringRequest beceCode) {
      return  departmentServiceInterface.getDepartmentByInstitution(beceCode);
    }




//================================= LOOKUPS ============================================================================

    @PostMapping("/api/setup/addLookUp")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitLookup(@RequestBody LookupRequest lookupRequest) {
        lookupServiceInterface.createLookup(lookupRequest);
    }
    @PostMapping("/api/setup/addLookUps")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<LookupResponse>>  SubmitLookupList(@RequestBody List<LookupRequest> lookupRequest) {
        return  lookupServiceInterface.createLookups(lookupRequest);
    }

    @PostMapping("/api/setup/getLookUpByType")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<LookupResponse>> getLookUpType(@RequestBody SingleStringRequest lookupType) {
       return lookupServiceInterface.getAllLookupsByType(lookupType);
    }

    @PostMapping("/api/setup/getAllLookUp")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<LookupResponse>> getAllLookUps() {
        return lookupServiceInterface.getAllLookups();
    }

//================================== CLASSES ===========================================================================

    @PostMapping("/api/setup/addClasses")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<Optional<ClassesResponse>>> AddClasses(@RequestBody ClassesRequest classesRequest) {
        return classesServiceInterface.createClasses(classesRequest);
    }

    @PostMapping("/api/setup/getInstitutionClasses")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<ClassesResponse>> getClasses(@RequestBody SingleStringRequest beceCode) {
       return classesServiceInterface.getAllClassesByInstitution(beceCode);
    }

    @PostMapping("/api/setup/getInstitutionClassesByClassGroup")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<Optional<ClassesResponse>>> getClassesAndClassGroup(@RequestBody ClassGroupRequest beceCode) {
        return classesServiceInterface.getAllClassesByClassGroup(beceCode);
    }



//================================== SUBJECTS ==========================================================================

    @PostMapping("/api/setup/addSubjects")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<Optional<SubjectResponse>>> AddSubjects(@RequestBody SubjectRequest subjectRequest) {
        return subjectServiceInterface.createSubject(subjectRequest);
    }

    @PostMapping("/api/setup/getInstitutionSubjects")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<SubjectResponse>> GetSubjects(@RequestBody SingleStringRequest beceCode) {
       return subjectServiceInterface.getAllSubjectsByInstitution(beceCode);
    }

    @PostMapping("/api/setup/getInstitutionSubjectsAndClassGroup")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<Optional<SubjectResponse>>> GetSubjects(@RequestBody SubjectDetails json) {
        return subjectServiceInterface.getAllSubjectsByInstitutionAndClassGroup(json);
    }



//================================== GRADING SETTING ===================================================================


    @PostMapping("/api/setup/addGradingSetting")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<GradingSettingResponse> AddGradingSetting(@RequestBody GradingSettingRequest gradingSettingRequests) {
      return  gradingSettingsServiceInterface.createGradingSettingDetails(gradingSettingRequests);
    }

    @PostMapping("/api/setup/getInstitutionGradingSetting")
    @ResponseStatus(HttpStatus.OK)
    public Optional<GradingSettingResponse> GetGradingSettingByCode(@RequestBody SingleStringRequest beceCode) {
        Optional<GradingSettingResponse> GetGradingSettingByCode = gradingSettingsServiceInterface.getAllGradingSettingsByInstitution(beceCode);
        return GetGradingSettingByCode;
    }


//====================================== DESIGNATION ===================================================================


    @PostMapping("/api/setup/addDesignations")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<Optional<DesignationResponse>>> AddDesignation(@RequestBody DesignationRequest designationRequest) {
      return  designationServiceInterface.createDesignation(designationRequest);
    }

    @PostMapping("/api/setup/getInstitutionDesignations")
    @ResponseStatus(HttpStatus.OK)
    public  Optional<List<List<Optional<DesignationResponse>>>> GetDesignationByInstitution(@RequestBody SingleStringRequest designationRequest) {
        return designationServiceInterface.getAllDesignationByInstitution(designationRequest);
    }



//======================================== JOB DESCRIPTION =============================================================

    @PostMapping("/api/setup/addJobDescription")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<JobDescriptionResponse> AddJobDescription(@RequestBody JobDescriptionRequest jobDescriptionRequest) {
      return  jobDescriptionServiceInterface.createJobDescriptions(jobDescriptionRequest);
    }

    @PostMapping("/api/setup/getInstitutionJobDescriptions")
    @ResponseStatus(HttpStatus.OK)
    public List<List<List<Optional<JobDescriptionResponse>>>> getInstitutionJobDescriptions(@RequestBody SingleStringRequest beceCode) {
        return jobDescriptionServiceInterface.getAllJobDescriptionsByInstitution(beceCode);
    }




//======================================== ADMISSIONS =============================================================

    @PostMapping("/api/setup/addAdmissionSetup")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<AdmissionsResponse> AddAdmission(@RequestBody AdmissionsEntryRequest admissionsEntryRequest) {
        return  admissionsServiceInterface.createAdmissionSetup(admissionsEntryRequest);
    }

    @PostMapping("/api/setup/getInstitutionAdmissionSetup")
    @ResponseStatus(HttpStatus.OK)
    public Optional<AdmissionsResponse> getInstitutionAdmissionSetup(@RequestBody SingleStringRequest beceCode) {
        return admissionsServiceInterface.getAllAdmissionSetupByInstitution(beceCode);
    }



//______________________________________________________________________________________________________________________

    public String fallBack0(InstitutionRequest institutionRequest,RuntimeException runtimeException){
        return "Temporal Failure, Try again after sometime";
    }
}
