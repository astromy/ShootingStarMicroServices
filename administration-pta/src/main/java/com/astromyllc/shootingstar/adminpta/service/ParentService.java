package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentAccountRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.alien.DynamicStringRequestUtil;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentAccountResponse;
import com.astromyllc.shootingstar.adminpta.model.StudentAccount;
import com.astromyllc.shootingstar.adminpta.serviceInterface.ParentServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.ParentsUtil;
import com.astromyllc.shootingstar.adminpta.util.StudentAccountUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParentService implements ParentServiceInterface {
    private static final int REACTIVATION_FEE_GHS = 50;
    private static final double REACTIVATION_FEE_PESEWAS = REACTIVATION_FEE_GHS * 100.0;
    private static final int MAX_ACTIVATION_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1500;
    private final ParentsUtil parentsUtil;
    private final StudentAccountUtil studentAccountUtil;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final ObjectMapper objectMapper;
    @Value("${paystack.secrete}")
    private String paystackSecretKey;

    @Override
    public Optional<String> activateStudentAccount(DynamicStringRequest request) {
        // Validate input
        if (request == null || request.getKey() == null || request.getVal() == null) {
            log.warn("Invalid activation request - null input");
            return Optional.empty();
        }

        // Get required fields
        String studentId = DynamicStringRequestUtil.getValue(request, "studentId");
        String email = DynamicStringRequestUtil.getValue(request, "email");


        try {
            String activationLink = String.format(
                    "https://payments.astromyllc.com?studentId=%s&email=%s&amount=%s",
                    URLEncoder.encode(studentId, StandardCharsets.UTF_8),
                    URLEncoder.encode(email, StandardCharsets.UTF_8),
                    URLEncoder.encode(String.valueOf(REACTIVATION_FEE_GHS), StandardCharsets.UTF_8)
            );

            String mailBody = """
                    <html>
                        <body>
                            <p>Hello,</p>
                            <p>Welcome! By clicking on the Activation link, you will be charged for the reactivation of the account of your ward.</p>
                            <p><a href='%s'>Click HERE to activate Account</a></p>
                            <p>If you have any questions or need help getting started, we're just a message away.</p>
                            <p>Here's to a great partnership! 🚀</p>
                            <p>Warm regards,<br>
                            support@astromyllc.com<br>
                            Astromy ORB Services</p>
                        </body>
                    </html>
                    """.formatted(activationLink);

            boolean emailSent = parentsUtil.sendmail(email, mailBody);

            return emailSent
                    ? Optional.of("Activation email sent successfully to " + email)
                    : Optional.of("Failed to send activation email");

        } catch (Exception e) {
            log.error("Failed to process activation for student {}", studentId, e);
            return Optional.of("Account activation process failed");
        }
    }

    @Override
    public Optional<String> subscriptionPaymentStatus(PaystackPaymentResponse request) {
        String paymentStatus = request.getData().getStatus();
        Double paymentAmount = request.getData().getAmount();
        String reference = request.getData().getReference();

        if (paymentStatus == null || paymentAmount == null
                || !paymentStatus.equalsIgnoreCase("success")
                || paymentAmount != REACTIVATION_FEE_PESEWAS) {
            log.warn("Ignoring webhook: status={}, amount={}, reference={}", paymentStatus, paymentAmount, reference);
            return Optional.empty();
        }

        String studentID = request.getData().getMetadata().getCustomFields().get(0).getValue();

        boolean alreadyActive = StudentAccountUtil.studentAccountsGlobalList.stream()
                .anyMatch(sa -> sa.getStudentId().equalsIgnoreCase(studentID)
                        && "active".equalsIgnoreCase(sa.getActivationState()));

        if (alreadyActive) {
            log.info("Ignoring duplicate webhook for already-active student {} (reference={})", studentID, reference);
            return Optional.of("Account already active for " + studentID);
        }

        List<StudentAccount> sa = new ArrayList<>();
        sa.add(StudentAccountUtil.mapStudentAccountRequest_ToStudentAccount(
                new StudentAccountRequest(studentID, "active"), studentID));
        studentAccountUtil.saveAll(sa);

        return Optional.of("Account Reactivated for " + studentID);
    }


    @Override
    public Optional<StudentAccountResponse> getActivationStatus(String studentId) {
        return StudentAccountUtil.studentAccountsGlobalList.stream()
                .filter(sa -> sa.getStudentId().equalsIgnoreCase(studentId))
                .reduce((first, second) -> second)
                .map(StudentAccountUtil::mapStudentAccount_ToStudentAccountResponse);
    }


    @Override
    public Map<String, Object> verifyPayment(String reference, String studentId) {
        Map<String, Object> result = new HashMap<>();

        boolean paystackConfirmed;
        try {
            paystackConfirmed = confirmWithPaystack(reference);
        } catch (Exception e) {
            log.error("Paystack verification call failed for reference {}", reference, e);
            result.put("success", false);
            result.put("message", "Could not reach Paystack for verification");
            return result;
        }

        if (!paystackConfirmed) {
            result.put("success", false);
            result.put("message", "Payment not confirmed by Paystack");
            return result;
        }

        boolean activated = waitForActivation(studentId);
        result.put("success", activated);
        if (!activated) {
            result.put("message", "Payment confirmed but activation is still pending. Please refresh shortly.");
        }
        return result;
    }

    private boolean confirmWithPaystack(String reference) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.paystack.co/transaction/verify/" + reference))
                .header("Authorization", "Bearer " + paystackSecretKey)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Map<?, ?> paystackResult = objectMapper.readValue(response.body(), Map.class);
        Map<?, ?> data = (Map<?, ?>) paystackResult.get("data");
        return data != null && "success".equals(data.get("status"));
    }

    private boolean waitForActivation(String studentId) {
        for (int attempt = 1; attempt <= MAX_ACTIVATION_RETRIES; attempt++) {
            boolean active = StudentAccountUtil.studentAccountsGlobalList.stream()
                    .anyMatch(sa -> sa.getStudentId().equalsIgnoreCase(studentId)
                            && "active".equalsIgnoreCase(sa.getActivationState()));
            if (active) return true;
            if (attempt < MAX_ACTIVATION_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }

}