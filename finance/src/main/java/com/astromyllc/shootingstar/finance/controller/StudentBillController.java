package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.BillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillRequest;
import com.astromyllc.shootingstar.finance.dto.request.StudentBillFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.Student_BillRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillResponse;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;
import com.astromyllc.shootingstar.finance.serviceInterface.BillServiceInterface;
import com.astromyllc.shootingstar.finance.serviceInterface.Student_BillServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentBillController {

    private final Student_BillServiceInterface studentBillServiceInterface;

    @PostMapping
    @RequestMapping("/api/finance/create-studentBill")
    @ResponseStatus(HttpStatus.CREATED)
    public Student_BillResponse createStudentBill(@RequestBody Student_BillRequest studentBillRequest) {
        log.info("Application  Received");
        return studentBillServiceInterface.createStudentBill(studentBillRequest);
    }

    @PostMapping
    @RequestMapping("/api/finance/create-studentsBill")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Student_BillResponse> createStudentsBill(@RequestBody List<Student_BillRequest> studentBillRequest) {
        log.info("Application  Received");
        return studentBillServiceInterface.createStudentsBill(studentBillRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/getStudentBillsByInstitution")
    @ResponseStatus(HttpStatus.OK)
    public List<Student_BillResponse> getStudentBillsByInstitution(@RequestBody StudentBillFetchRequest studentBillFetchRequest) {
        log.info("Application  Received");
        return studentBillServiceInterface.fetchStudentBillsByInstitution(studentBillFetchRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/getStudentBillByIdAndInstitution")
    @ResponseStatus(HttpStatus.OK)
    public Student_BillResponse getStudentBillsByInstitutionandStudent(@RequestBody StudentBillFetchRequest studentBillFetchRequest) {
        log.info("Application  Received");
        return studentBillServiceInterface.fetchStudentBillByIdAndInstitution(studentBillFetchRequest);
    }


    @PostMapping
    @RequestMapping("/api/finance/getOwingStudentsInstitution")
    @ResponseStatus(HttpStatus.OK)
    public List<Student_BillResponse> getOwingByInstitution(@RequestBody StudentBillFetchRequest studentBillFetchRequest) {
        log.info("Application  Received");
        return studentBillServiceInterface.fetchOwingStudentsByInstitution(studentBillFetchRequest);
    }

}
