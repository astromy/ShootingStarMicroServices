package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.StudentBillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.Student_BillServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentBillController {

    private final Student_BillServiceInterface studentBillServiceInterface;

    @PostMapping
    @RequestMapping("/api/finance/create-studentBill")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Optional<Student_BillResponse>> createStudentBill(@RequestBody Student_BillRequest studentBillRequest) {
        log.info("Application  Received");
        return ResponseEntity.ok(Optional.ofNullable(studentBillServiceInterface.createStudentBill(studentBillRequest)));
    }


    @PostMapping
    @RequestMapping("/api/finance/create-studentsBill")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Optional<List<Student_BillResponse>>> createStudentsBill(@RequestBody List<Student_BillRequest> studentBillRequest) {
        log.info("Application  Received");
        return ResponseEntity.ok(Optional.ofNullable(studentBillServiceInterface.createStudentsBill(studentBillRequest)));
    }


    @PostMapping
    @RequestMapping("/api/finance/getStudentBillsByInstitution")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<Student_BillResponse>>> getStudentBillsByInstitution(@RequestBody StudentBillFetchRequest studentBillFetchRequest) {
        log.info("Application  Received");
        return ResponseEntity.ok(studentBillServiceInterface.fetchStudentBillsByInstitution(studentBillFetchRequest));
    }


    @PostMapping
    @RequestMapping("/api/finance/getStudentBillsByInstitutionClass")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<Student_BillResponse>>> getStudentBillsByInstitutionClass(@RequestBody StudentBillFetchRequest studentBillFetchRequest) {
        log.info("Application  Received");
        return ResponseEntity.ok(studentBillServiceInterface.fetchStudentBillsByInstitutionClass(studentBillFetchRequest));
    }


    @PostMapping
    @RequestMapping("/api/finance/getStudentBillByIdAndInstitution")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<Student_BillResponse>> getStudentBillsByInstitutionandStudent(@RequestBody StudentBillFetchRequest studentBillFetchRequest) {
        log.info("Application  Received");
        return ResponseEntity.ok(studentBillServiceInterface.fetchStudentBillByIdAndInstitution(studentBillFetchRequest));
    }


    @PostMapping
    @RequestMapping("/api/finance/getOwingStudentsInstitution")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<List<Student_BillResponse>>> getOwingByInstitution(@RequestBody StudentBillFetchRequest studentBillFetchRequest) {
        log.info("Application  Received");
        return ResponseEntity.ok(studentBillServiceInterface.fetchOwingStudentsByInstitution(studentBillFetchRequest));
    }

}
