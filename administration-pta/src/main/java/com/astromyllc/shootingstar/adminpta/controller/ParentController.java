package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentAccountResponse;
import com.astromyllc.shootingstar.adminpta.serviceInterface.ParentServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.PaystackSignatureVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParentController {
    private final ParentServiceInterface parentServiceInterface;
    private final PaystackSignatureVerifier paystackSignatureVerifier;
    private final ObjectMapper objectMapper;

    @PostMapping("/api/administration-pta/sendReactivationEmail")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<String>> sendReactivationEmail(@RequestBody DynamicStringRequest request) {
        return ResponseEntity.ok(parentServiceInterface.activateStudentAccount(request));
    }

    @PostMapping("/api/administration-pta/getStudentActivationStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<StudentAccountResponse>> getActivationStatus(@RequestBody SingleStringRequest request) {
        return ResponseEntity.ok(parentServiceInterface.getActivationStatus(request.getVal()));
    }

    @PostMapping("/api/administration-pta/subscriptionPaymentStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Optional<String>> subscriptionPaymentStatus(
            @RequestBody String rawBody,
            @RequestHeader(value = "x-paystack-signature", required = false) String signature) {

        if (!paystackSignatureVerifier.isValid(rawBody, signature)) {
            log.warn("Rejected Paystack webhook: invalid or missing signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Optional.of("Invalid signature"));
        }

        try {
            PaystackPaymentResponse request = objectMapper.readValue(rawBody, PaystackPaymentResponse.class);
            return ResponseEntity.ok(parentServiceInterface.subscriptionPaymentStatus(request));
        } catch (Exception e) {
            log.error("Failed to parse Paystack webhook payload", e);
            return ResponseEntity.badRequest().body(Optional.of("Malformed payload"));
        }
    }

}
