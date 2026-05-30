package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.StudentAccountRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.alien.DynamicStringRequestUtil;
import com.astromyllc.shootingstar.adminpta.model.StudentAccount;
import com.astromyllc.shootingstar.adminpta.serviceInterface.ParentServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.ParentsUtil;
import com.astromyllc.shootingstar.adminpta.util.StudentAccountUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParentService implements ParentServiceInterface {
 private final ParentsUtil parentsUtil;
    private final StudentAccountUtil studentAccountUtil;


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
                    "https://payments.astromyllc.com?studentId=%s&email=%s",
                    URLEncoder.encode(studentId, StandardCharsets.UTF_8),
                    URLEncoder.encode(email, StandardCharsets.UTF_8)
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
        String paymentStatus=request.getData().getStatus();
        Double paymentAmount=request.getData().getAmount();
        if(paymentStatus.equalsIgnoreCase("success") && paymentAmount==(5000)){
           String studentID= request.getData().getMetadata().getCustomFields().get(0).getValue();
           String status="active";
            List<StudentAccount> sa= new ArrayList<>();
            sa.add(StudentAccountUtil.mapStudentAccountRequest_ToStudentAccount(new StudentAccountRequest(studentID,status),studentID));
            studentAccountUtil.saveAll(sa);

            return Optional.of("Account Reactivated for "+ studentID);
        }
        return Optional.empty();
    }


}
