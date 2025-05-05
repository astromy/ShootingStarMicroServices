package com.astromyllc.shootingstar.hr.utils;


import com.astromyllc.shootingstar.hr.config.EmailProcessingException;
import com.astromyllc.shootingstar.hr.config.RateLimitExceededException;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class MailUtil {
    private final JavaMailSender mailSender;
    private static final RateLimiter rateLimiter = RateLimiter.create(10.0); // Zepto allows higher rates

    @Value("${spring.mail.username}")
    private String fromEmail;


    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void sendTransactionalEmail(String to, String subject, String htmlContent,String fromEmail) {
        if (!rateLimiter.tryAcquire()) {
            throw new RateLimitExceededException("ZeptoMail rate limit exceeded");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail+"@astromyllc.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = isHTML

            mailSender.send(message);
            log.info("Sent ZeptoMail to {}", to);
        } catch (MessagingException e) {
            throw new EmailProcessingException("Failed to construct email", e);
        }
    }

    @Recover
    public void handleFailedEmail(Exception e, String to, String subject, String body) {
        log.error("ZeptoMail failed after retries to {}: {}", to, e.getMessage());
        // Implement your fallback logic here
    }
}