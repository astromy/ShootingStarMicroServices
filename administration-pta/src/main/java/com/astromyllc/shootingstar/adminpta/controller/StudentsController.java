package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.dto.request.AdmissionRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.serviceInterface.StudentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StudentsController {
    private final StudentServiceInterface studentServiceInterface;

    @PostMapping("/api/administration-pta/conduct-admissions")
    @ResponseStatus(HttpStatus.OK)
    public void conductAdmissions(@RequestBody AdmissionRequest admissionRequest) {
        studentServiceInterface.fetchCurrentApplications(admissionRequest);
    }
}
