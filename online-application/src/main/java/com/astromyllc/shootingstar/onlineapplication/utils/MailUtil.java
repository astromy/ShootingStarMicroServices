package com.astromyllc.shootingstar.onlineapplication.utils;


import com.astromyllc.shootingstar.onlineapplication.config.EmailProcessingException;
import com.astromyllc.shootingstar.onlineapplication.config.RateLimitExceededException;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.PostConstruct;
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
    @Value("${app.mail.sender}")
    private String senderEmail;


    @PostConstruct
    public void debugMailConfig() {
        log.info("SMTP username: [{}]", fromEmail);
        log.info("SMTP sender: [{}]", senderEmail);
    }

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
                // ✅ These headers tell mail clients this is high priority
                message.addHeader("X-Priority", "1");           // 1=High, 3=Normal, 5=Low
                message.addHeader("X-MSMail-Priority", "High"); // Outlook
                message.addHeader("Importance", "High");        // RFC 2156
                message.addHeader("Priority", "urgent");        // Some clients
            }

            mailSender.send(message);
            log.info("Sent email to {}", to);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailProcessingException("Failed to send email to " + to, e);
        }
    }

    public boolean sendTransactionalEmail(String to, String subject,
                                          String htmlContent, String fromName) {
        return sendTransactionalEmail(to, subject, htmlContent, fromName, false);
    }

    @Recover
    public boolean handleFailedEmail(Exception e, String to, String subject, String htmlContent, String fromEmail) {
        log.error("ZeptoMail failed after retries to {}: {}", to, e.getMessage());
        // Fallback logic (e.g., queue for later retry or alternative provider)
        return false;
    }

}