package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.adminpta.dto.request.*;
import com.astromyllc.shootingstar.adminpta.dto.response.ClassListResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSkimResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentSkimWithParentResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.serviceInterface.ParentServiceInterface;
import com.astromyllc.shootingstar.adminpta.serviceInterface.StudentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParentController {
    private final ParentServiceInterface parentServiceInterface;

    @PostMapping("/api/administration-pta/sendReactivationEmail")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<String>> sendReactivationEmail(@RequestBody DynamicStringRequest request) {
        return ResponseEntity.ok(parentServiceInterface.activateStudentAccount(request));
    }

    @PostMapping("/api/administration-pta/subscriptionPaymentStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<String>> subscriptionPaymentStatus(@RequestBody PaystackPaymentResponse request) {
        return ResponseEntity.ok(parentServiceInterface.subscriptionPaymentStatus(request));
    }

}
