package com.astromyllc.shootingstar.setup.utils;


import com.astromyllc.shootingstar.setup.config.EmailProcessingException;
import com.astromyllc.shootingstar.setup.config.RateLimitExceededException;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailUtil {
    private static final RateLimiter rateLimiter = RateLimiter.create(10.0); // Zepto allows higher rates
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${app.mail.sender}")  // ✅ your actual verified sender address
    private String senderEmail;


    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public boolean sendTransactionalEmail(String to, String subject,
                                          String htmlContent,
                                          String fromName,
                                          boolean isImportant) {
        if (!rateLimiter.tryAcquire()) {
            throw new RateLimitExceededException("ZeptoMail rate limit exceeded");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, fromEmail);
            helper.setReplyTo(senderEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            message.setFrom(new jakarta.mail.internet.InternetAddress(
                    senderEmail, fromName, "UTF-8"
            ));

            if (isImportant) {
                message.addHeader("X-Priority", "1");
                message.addHeader("X-MSMail-Priority", "High");
                message.addHeader("Importance", "High");
                message.addHeader("Priority", "urgent");
            }

            mailSender.send(message);
            log.info("Sent ZeptoMail to {}", to);
            return true;
        } catch (MessagingException e) {
            throw new EmailProcessingException("Failed to construct email", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendTransactionalEmail(String to, String subject,
                                          String htmlContent, String fromName) {
        return sendTransactionalEmail(to, subject, htmlContent, fromName, false);
    }

    @Recover
    public void handleFailedEmail(Exception e, String to, String subject, String body) {
        log.error("ZeptoMail failed after retries to {}: {}", to, e.getMessage());
        // Implement your fallback logic here
    }
}