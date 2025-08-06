package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.dto.request.DynamicStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.alien.DynamicStringRequestUtil;
import com.astromyllc.shootingstar.adminpta.dto.response.StudentsResponse;
import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.serviceInterface.ParentServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.ParentsUtil;
import com.astromyllc.shootingstar.adminpta.util.StudentUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParentService implements ParentServiceInterface {
 private final ParentsUtil parentsUtil;


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



}
