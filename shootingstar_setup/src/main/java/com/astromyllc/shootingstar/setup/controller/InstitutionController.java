package com.astromyllc.shootingstar.setup.controller;

import com.astromyllc.shootingstar.setup.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.setup.dto.request.*;
import com.astromyllc.shootingstar.setup.dto.response.*;
import com.astromyllc.shootingstar.setup.serviceInterface.*;
import com.astromyllc.shootingstar.setup.utils.PaystackSignatureVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
    private final PromotionsServiceInterface promotionsServiceInterface;
    private final PaystackSignatureVerifier paystackSignatureVerifier;
    private final GeoCoordinateServiceInterface geoCoordinateServiceInterface;
    private final BusServiceInterface busServiceInterface;
    private final RouteServiceInterface routeServiceInterface;
    private final ObjectMapper objectMapper;

//=============================== INSTITUTION ========================================================

    /**
     * @param institutionRequest
     */
    @PostMapping("/api/setup/signupInstitution")
    @ResponseStatus(HttpStatus.CREATED)
    //@CircuitBreaker(name = "Institution",fallbackMethod = "fallBack0")
    public InstitutionResponse SubmitApplication(@RequestBody InstitutionRequest institutionRequest) throws IOException {
        log.error("REQUEST INSTITUTION CREATION OF..... {}", institutionRequest);
        return institutionService.createInstitution(institutionRequest);
    }

    @PostMapping("/api/setup/getAllinstitution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<InstitutionResponse>> getAllInstitution() {
        log.error("REQUEST getAllinstitution OF..... ");
        return institutionService.getAllInstitution();
    }

    @PostMapping("/api/setup/getAllinstitutionForWeb")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<InstitutionResponse>> getAllinstitutionForWeb() {
        log.error("REQUEST getAllinstitution OF..... ");
        return institutionService.getAllinstitutionForWeb();
    }

    @PostMapping("/api/setup/getAllSubscribedInstitution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<InstitutionResponse>> getAllSubscribedInstitution() {
        return institutionService.getAllSubscribedInstitution();
    }

    @PostMapping("/api/setup/getInstitutionByCode")
    @ResponseStatus(HttpStatus.OK)
    public Optional<InstitutionResponse> getInstitutionByBeceCode(@RequestBody SingleStringRequest beceCode) throws IOException {
        return institutionService.getInstitutionByBeceCode(beceCode);
    }

    @PostMapping("/api/setup/getInstitutionStatus")
    @ResponseStatus(HttpStatus.OK)
    public Optional<SkimpInstitutionResponse> getInstitutionStatus(@RequestBody SingleStringRequest beceCode) throws IOException {
        return institutionService.getInstitutionStatus(beceCode);
    }

    @PostMapping("/api/setup/reactivateInstitutionalAccount")
    public ResponseEntity<Optional<String>> reactivateInstitutionalAccount(
            @RequestBody String rawBody,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature) {

        /*if (!paystackSignatureVerifier.isValid(rawBody, signature)) {
            log.warn("Rejected Paystack webhook: invalid or missing signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Optional.of("Invalid signature"));
        }*/

        try {
            PaystackPaymentResponse paystack = objectMapper.readValue(rawBody, PaystackPaymentResponse.class);
            return ResponseEntity.ok(institutionService.reactivateInstitutionalAccount(paystack));
        } catch (Exception e) {
            log.error("Failed to parse Paystack webhook payload", e);
            return ResponseEntity.badRequest().body(Optional.of("Malformed payload"));
        }
    }

    @PostMapping("/api/setup/getUpgradeQuote")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUpgradeQuote(@RequestBody UpgradeQuoteRequest request) {
        try {
            return ResponseEntity.ok(institutionService.getUpgradeQuote(request));
        } catch (IllegalArgumentException e) {
            log.warn("Rejected upgrade quote request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Optional.of(e.getMessage()));
        } catch (IOException e) {
            log.error("Failed to compute upgrade quote for {}", request.getInstitutionCode(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Optional.of("Failed to compute upgrade quote"));
        }
    }

    @PostMapping("/api/setup/upgradeSubscriptionPaymentStatus")
    public ResponseEntity<Optional<String>> upgradeSubscriptionPaymentStatus(
            @RequestBody String rawBody,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature) {

        /*if (!paystackSignatureVerifier.isValid(rawBody, signature)) {
            log.warn("Rejected Paystack upgrade webhook: invalid or missing signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Optional.of("Invalid signature"));
        }*/

        try {
            PaystackPaymentResponse paystack = objectMapper.readValue(rawBody, PaystackPaymentResponse.class);
            return ResponseEntity.ok(institutionService.upgradeSubscriptionPaymentStatus(paystack));
        } catch (Exception e) {
            log.error("Failed to parse Paystack upgrade webhook payload", e);
            return ResponseEntity.badRequest().body(Optional.of("Malformed payload"));
        }
    }

    @PostMapping("/api/setup/getGeofenceBoundary")
    @ResponseStatus(HttpStatus.OK)
    public GeofenceBoundaryResponse getInstitutionByBeceCodePath(@RequestBody SingleStringRequest beceCode) throws IOException {
        log.error("REQUEST getGeofenceBoundary OF..... {}", beceCode);
        // Now returns every campus's boundary grouped by name, not just one
        // flat list - a single-campus institution just gets a one-entry list
        // named "Main Campus", so existing single-campus callers still work.
        return geoCoordinateServiceInterface.getGeofenceBoundariesByInstitution(beceCode.getVal());
    }

    @PostMapping("/api/setup/getCampusNames")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getCampusNames(@RequestBody SingleStringRequest beceCode) {
        return geoCoordinateServiceInterface.getCampusNames(beceCode.getVal());
    }

    @PostMapping("/api/setup/deleteCampus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<String>> deleteCampus(@RequestBody SaveGeofenceBoundaryRequest request) {
        boolean deleted = geoCoordinateServiceInterface.deleteCampus(request.getInstitution(), request.getCampusName());
        if (!deleted) {
            return ResponseEntity.badRequest().body(Optional.of("No such campus to delete."));
        }
        return ResponseEntity.ok(Optional.of("Campus deleted."));
    }

    @PostMapping("/api/setup/saveGeofenceBoundary")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> AddGeoCoordinates(@RequestBody SaveGeofenceBoundaryRequest request) {
        log.error("REQUEST AddGeoCoordinates OF..... {}", request);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(geoCoordinateServiceInterface.addGeoCoordinates(request));
        } catch (IllegalArgumentException e) {
            // Thrown by GeoCoordinateService#assertCanAddCampus - a non-Enterprise
            // institution trying to add a second distinct campus. Same
            // {error: "UPGRADE_REQUIRED", ...} shape SubscriptionEnforcementInterceptor
            // uses on the astro-orb side, so mobile's classifyError() recognizes this
            // as an upgrade prompt rather than falling through to its generic 403 ->
            // AUTH_ERROR handling (which would otherwise force-log the user out).
            log.warn("Rejected campus add: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "UPGRADE_REQUIRED",
                    "message", e.getMessage(),
                    "requiredPlan", "ENTERPRISE"
            ));
        }
    }

    @PostMapping("/api/setup/updateGeofenceBoundary")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> UpdateGeoCoordinates(@RequestBody SaveGeofenceBoundaryRequest request) {
        log.error("REQUEST UpdateGeoCoordinates OF..... {}", request);
        try {
            return ResponseEntity.ok(geoCoordinateServiceInterface.updateGeoCoordinates(request));
        } catch (IllegalArgumentException e) {
            log.warn("Rejected campus update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "UPGRADE_REQUIRED",
                    "message", e.getMessage(),
                    "requiredPlan", "ENTERPRISE"
            ));
        }
    }

    @PostMapping("/api/setup/getInstitutionPopulation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInstitutionPopulation(@RequestBody SingleStringRequest request) {
        try {
            return ResponseEntity.ok(institutionService.getInstitutionPopulation(request));
        } catch (IllegalArgumentException e) {
            log.warn("Rejected population lookup: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Optional.of(e.getMessage()));
        } catch (IOException e) {
            log.error("Failed to fetch population for {}", request.getVal(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Optional.of("Failed to fetch institution population"));
        }
    }

    //========================== BUSES (TRANSPORT) ===============================================

    @PostMapping("/api/setup/addBuses")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<BusResponse>> addBuses(@RequestBody BusRequest busRequest) {
        log.info("REQUEST addBuses OF..... {}", busRequest);
        return busServiceInterface.createBuses(busRequest);
    }

    @PostMapping("/api/setup/getInstitutionBuses")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<BusResponse>> getInstitutionBuses(@RequestBody SingleStringRequest beceCode) {
        log.info("REQUEST getInstitutionBuses OF..... {}", beceCode);
        return busServiceInterface.getBusesByInstitution(beceCode);
    }

    @PostMapping("/api/setup/updateBus")
    @ResponseStatus(HttpStatus.OK)
    public Optional<BusResponse> updateBus(@RequestBody BusDetails busDetails) {
        log.info("REQUEST updateBus OF..... {}", busDetails);
        return busServiceInterface.updateBus(busDetails);
    }

    //========================== ROUTES (TRANSPORT) ===============================================

    @PostMapping("/api/setup/addRoutes")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<RouteResponse>> addRoutes(@RequestBody RouteRequest routeRequest) {
        log.info("REQUEST addRoutes OF..... {}", routeRequest);
        return routeServiceInterface.createRoutes(routeRequest);
    }

    @PostMapping("/api/setup/getInstitutionRoutes")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<RouteResponse>> getInstitutionRoutes(@RequestBody SingleStringRequest beceCode) {
        log.info("REQUEST getInstitutionRoutes OF..... {}", beceCode);
        return routeServiceInterface.getRoutesByInstitution(beceCode);
    }


    //========================== PRE-REQUEST ===============================================
    @PostMapping("/api/setup/preRequestInstitution")
    @ResponseStatus(HttpStatus.CREATED)
    public String SubmitPreOrderApplication(@RequestBody PreOrderInstitutionRequest institutionRequest) throws IOException {
        log.info("REQUEST SubmitPreOrderApplication OF..... {}", institutionRequest);
        return institutionService.createPreOrderInstitution(institutionRequest);
    }

    @PostMapping("/api/setup/getPreOrderAllinstitution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<PreOrderInstitutionResponse>> getAllPreOrderedInstitution() {
        log.error("REQUEST getPreOrderAllinstitution OF..... ");
        return institutionService.getAllPreOrderedInstitution();
    }

    @PostMapping("/api/setup/migratePreOrder")
    @ResponseStatus(HttpStatus.CREATED)
    public InstitutionResponse migratePreOrder(@RequestBody SingleStringRequest beceCode) throws IOException {
        log.error("REQUEST migratePreOrder OF..... {}", beceCode);
        return institutionService.migratePreOrder(beceCode.getVal());
    }


    //================================== DEPARTMENTS ====================================================

    @PostMapping("/api/setup/addDepartment")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<DepartmentResponse>> AddDepartment(@RequestBody DepartmentRequest departmentRequest) {
        log.error("REQUEST AddDepartment OF..... {}", departmentRequest);
        return departmentServiceInterface.createDepartments(departmentRequest);
    }

    @PostMapping("/api/setup/getInstitutionDepartment")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<DepartmentResponse>> getDepartmentByInstitution(@RequestBody SingleStringRequest beceCode) {
        log.error("REQUEST getDepartmentByInstitution OF..... {}", beceCode);
        return departmentServiceInterface.getDepartmentByInstitution(beceCode);
    }


//================================= LOOKUPS ============================================================================

    @PostMapping("/api/setup/addLookUp")
    @ResponseStatus(HttpStatus.CREATED)
    public void SubmitLookup(@RequestBody LookupRequest lookupRequest) {
        log.error("REQUEST SubmitLookup OF..... {}", lookupRequest);
        lookupServiceInterface.createLookup(lookupRequest);
    }

    @PostMapping("/api/setup/addLookUps")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<LookupResponse>> SubmitLookupList(@RequestBody List<LookupRequest> lookupRequest) {
        log.error("REQUEST SubmitLookupList OF..... {}", lookupRequest);
        return lookupServiceInterface.createLookups(lookupRequest);
    }

    @PostMapping("/api/setup/getLookUpByType")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<LookupResponse>> getLookUpType(@RequestBody SingleStringRequest lookupType) {
        log.info("LOOKUP REQUEST ===>{}", lookupType);
        return lookupServiceInterface.getAllLookupsByType(lookupType);
    }

    @PostMapping("/api/setup/getAllLookUp")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<LookupResponse>> getAllLookUps() {
        log.error("REQUEST getAllLookUps OF..... ");
        return lookupServiceInterface.getAllLookups();
    }

//================================== CLASSES ===========================================================================

    @PostMapping("/api/setup/addClasses")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<ClassesResponse>> AddClasses(@RequestBody ClassesRequest classesRequest) {
        log.error("REQUEST AddClasses OF..... {}", classesRequest);
        return classesServiceInterface.createClasses(classesRequest);
    }

    @PostMapping("/api/setup/getInstitutionClasses")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<ClassesResponse>> getClasses(@RequestBody SingleStringRequest beceCode) {
        log.error("REQUEST getClasses OF..... {}", beceCode);
        return classesServiceInterface.getAllClassesByInstitution(beceCode);
    }

    @PostMapping("/api/setup/getInstitutionClassesByClassGroup")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<ClassesResponse>> getClassesAndClassGroup(@RequestBody ClassGroupRequest beceCode) {
        log.error("REQUEST getClassesAndClassGroup OF..... {}", beceCode);
        return classesServiceInterface.getAllClassesByClassGroup(beceCode);
    }


//================================== SUBJECTS ==========================================================================

    @PostMapping("/api/setup/addSubjects")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<SubjectResponse>> AddSubjects(@RequestBody SubjectRequest subjectRequest) {
        log.error("REQUEST AddSubjects OF..... {}", subjectRequest);
        return subjectServiceInterface.createSubject(subjectRequest);
    }

    @PostMapping("/api/setup/getInstitutionSubjects")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<SubjectResponse>> GetSubjects(@RequestBody SingleStringRequest beceCode) {
        log.error("REQUEST GetSubjects OF..... {}", beceCode);
        return subjectServiceInterface.getAllSubjectsByInstitution(beceCode);
    }

    @PostMapping("/api/setup/getInstitutionSubjectsAndClassGroup")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<Optional<SubjectResponse>>> GetSubjects(@RequestBody SubjectDetails json) {
        log.error("REQUEST GetSubjects OF..... {}", json);
        return subjectServiceInterface.getAllSubjectsByInstitutionAndClassGroup(json);
    }


//================================== GRADING SETTING ===================================================================


    @PostMapping("/api/setup/addGradingSetting")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<GradingSettingResponse>> AddGradingSetting(@RequestBody GradingSettingRequest gradingSettingRequests) {
        log.error("REQUEST AddGradingSetting OF..... {}", gradingSettingRequests);
        return gradingSettingsServiceInterface.createGradingSettingDetails(gradingSettingRequests);
    }

    @PostMapping("/api/setup/getInstitutionGradingSetting")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<GradingSettingResponse>> GetGradingSettingByCode(@RequestBody SingleStringRequest beceCode) {
        log.error("REQUEST GetGradingSettingByCode OF..... {}", beceCode);
        Optional<List<GradingSettingResponse>> GetGradingSettingByCode = gradingSettingsServiceInterface.getAllGradingSettingsByInstitution(beceCode);
        return GetGradingSettingByCode;
    }


//====================================== DESIGNATION ===================================================================


    @PostMapping("/api/setup/addDesignations")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<Optional<DesignationResponse>>> AddDesignation(@RequestBody DesignationRequest designationRequest) {
        log.error("REQUEST AddDesignation OF..... {}", designationRequest);
        return designationServiceInterface.createDesignation(designationRequest);
    }

    @PostMapping("/api/setup/getInstitutionDesignations")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<List<Optional<DesignationResponse>>>> GetDesignationByInstitution(@RequestBody SingleStringRequest designationRequest) {
        log.error("REQUEST GetDesignationByInstitution OF..... {}", designationRequest);
        return designationServiceInterface.getAllDesignationByInstitution(designationRequest);
    }


//======================================== JOB DESCRIPTION =============================================================

    @PostMapping("/api/setup/addJobDescription")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<JobDescriptionResponse> AddJobDescription(@RequestBody JobDescriptionRequest jobDescriptionRequest) {
        log.error("REQUEST AddJobDescription OF..... {}", jobDescriptionRequest);
        return jobDescriptionServiceInterface.createJobDescriptions(jobDescriptionRequest);
    }

    @PostMapping("/api/setup/getInstitutionJobDescriptions")
    @ResponseStatus(HttpStatus.OK)
    public List<List<List<Optional<JobDescriptionResponse>>>> getInstitutionJobDescriptions(@RequestBody SingleStringRequest beceCode) {
        log.error("REQUEST getInstitutionJobDescriptions OF..... {}", beceCode);
        return jobDescriptionServiceInterface.getAllJobDescriptionsByInstitution(beceCode);
    }


//======================================== ADMISSIONS =============================================================

    @PostMapping("/api/setup/addAdmissionSetup")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<AdmissionsResponse> AddAdmission(@RequestBody AdmissionsEntryRequest admissionsEntryRequest) {
        return admissionsServiceInterface.createAdmissionSetup(admissionsEntryRequest);
    }

    @PostMapping("/api/setup/getInstitutionAdmissionSetup")
    @ResponseStatus(HttpStatus.OK)
    public Optional<AdmissionsResponse> getInstitutionAdmissionSetup(@RequestBody SingleStringRequest beceCode) {
        return admissionsServiceInterface.getAllAdmissionSetupByInstitution(beceCode);
    }


//================================== PROMOTIONS ========================================================================

    @PostMapping("/api/setup/addPromotionSetup")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Optional<PromotionsResponse>> AddPromotions(@RequestBody PromotionsRequest promotionsRequest) {
        return promotionsServiceInterface.createPromotions(promotionsRequest);
    }

    @PostMapping("/api/setup/getInstitutionPromotionSetup")
    @ResponseStatus(HttpStatus.OK)
    public List<Optional<PromotionsResponse>> getInstitutionPromotionSetup(@RequestBody SingleStringRequest beceCode) {
        return promotionsServiceInterface.getInstitutionPromotionSetup(beceCode);
    }


//______________________________________________________________________________________________________________________

    public String fallBack0(InstitutionRequest institutionRequest, RuntimeException runtimeException) {
        return "Temporal Failure, Try again after sometime";
    }
}