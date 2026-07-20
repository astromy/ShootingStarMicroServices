package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentAccountResponse;

import java.util.Map;
import java.util.Optional;

public interface ParentServiceInterface {

    Optional<String> activateStudentAccount(DynamicStringRequest request);

    Optional<String> subscriptionPaymentStatus(PaystackPaymentResponse request);

    Optional<StudentAccountResponse> getActivationStatus(String studentId);

    Map<String, Object> verifyPayment(String reference, String studentId);
}
