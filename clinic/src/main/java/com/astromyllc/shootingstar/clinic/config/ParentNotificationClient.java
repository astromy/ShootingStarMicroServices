package com.astromyllc.shootingstar.clinic.config;

import com.astromyllc.shootingstar.clinic.event.ParentNotificationPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@Slf4j
public class ParentNotificationClient {

    private final WebClient webClient;

    public ParentNotificationClient(
            @Value("${gateway.host}") String gatewayHost,
            WebClient.Builder builder) {
        this.webClient = builder.baseUrl(gatewayHost).build();
    }

    /**
     * POST /api/administration-pta/notifyParentsOfStudentEvent
     * Returns true on success, false on failure (caller decides how to retry).
     */
    public boolean notifyParentsOfStudentEvent(ParentNotificationPayload payload) {
        try {
            Map<?, ?> result = webClient.post()
                    .uri("/api/administration-pta/notifyParentsOfStudentEvent")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (result != null) {
                log.info("Parent notified for student {} at {} ({})",
                        payload.getStudentId(), payload.getInstitutionCode(), payload.getCategory());
                return true;
            }
        } catch (Exception ex) {
            log.error("Failed to notify parent for student {} at {}: {}",
                    payload.getStudentId(), payload.getInstitutionCode(), ex.getMessage());
        }
        return false;
    }
}