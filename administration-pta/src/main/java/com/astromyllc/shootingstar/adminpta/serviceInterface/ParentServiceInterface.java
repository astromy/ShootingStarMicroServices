package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;

import java.util.Optional;

public interface ParentServiceInterface {

    Optional<String> activateStudentAccount(DynamicStringRequest request);

    Optional<String>  subscriptionPaymentStatus(PaystackPaymentResponse request);
}
