package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.BillingFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.BillingsServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final BillingsServiceInterface billingsServiceInterface;

    /**
     * Primary billing endpoint called by _financeBilling.js → submitBilling().
     * Accepts: institutionCode, studentClass, term, academicYear,
     * studentId[] (list), billname[] (list).
     * Returns: updated Student_Bill list for the affected students.
     */
    @PostMapping("/bill-students-by-institution")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<Student_BillResponse>> billStudents(@RequestBody BillingsRequest request) {
        log.info("Billing request: institution={} class={} term={} year={} students={} bills={}",
                request.getInstitutionCode(), request.getStudentClass(),
                request.getTerm(), request.getAcademicYear(),
                request.getStudentId().size(), request.getBillname().size());
        return billingsServiceInterface.createBillings(request);
    }

    /**
     * Legacy alias kept for backward compatibility
     */
    @PostMapping("/create-billings")
    @ResponseStatus(HttpStatus.CREATED)
    public Optional<List<Student_BillResponse>> createBillings(@RequestBody BillingsRequest request) {
        return billingsServiceInterface.createBillings(request);
    }

    @PostMapping("/get-billings-by-institution")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getByInstitution(@RequestBody BillingFetchRequest request) {
        return billingsServiceInterface.fetchBillingsByInstitution(request);
    }

    @PostMapping("/get-billing-by-institution-and-student")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getByInstitutionAndStudent(@RequestBody BillingFetchRequest request) {
        return billingsServiceInterface.fetchBillingByInstitutionAndStudent(request);
    }

    /**
     * Used by Orb fee collection screen
     */
    @PostMapping("/getStudentBilling")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getStudentBilling(@RequestBody BillingFetchRequest request) {
        return billingsServiceInterface.fetchBillingByInstitutionStudentClassTerm(request);
    }

    /**
     * Used by Orb class billing view
     */
    @PostMapping("/getClassBilling")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getClassBilling(@RequestBody BillingFetchRequest request) {
        return billingsServiceInterface.fetchClassBillingByInstitution(request);
    }

    /**
     * Used by Orb school-wide billing report
     */
    @PostMapping("/getSchoolBilling")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getSchoolBilling(@RequestBody BillingFetchRequest request) {
        return billingsServiceInterface.fetchSchoolBillingByInstitution(request);
    }

    /**
     * Used by _financeBilling.js fetchExistingBillings()
     */
    @PostMapping("/get-billing-by-institutionClass")
    @ResponseStatus(HttpStatus.OK)
    public Optional<List<BillingsResponse>> getByInstitutionClass(@RequestBody BillingFetchRequest request) {
        return billingsServiceInterface.fetchClassBillingByInstitution(request);
    }
}