package com.astromyllc.shootingstar.hr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpoPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private final WebClient.Builder webClientBuilder;

    // Expo accepts a batch array of messages in one call. Best-effort: logs
    // and swallows failures rather than throwing, since a push failure
    // shouldn't block whatever triggered the notification (e.g. the
    // transport compliance scheduler should still send its emails even if
    // Expo is briefly unreachable).
    public void sendPush(List<String> expoPushTokens, String title, String body) {
        if (expoPushTokens == null || expoPushTokens.isEmpty()) {
            return;
        }

        List<Map<String, Object>> messages = expoPushTokens.stream()
                .map(token -> Map.<String, Object>of(
                        "to", token,
                        "title", title,
                        "body", body,
                        "sound", "default"
                ))
                .toList();

        try {
            webClientBuilder.build()
                    .post()
                    .uri(EXPO_PUSH_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(messages)
                    .retrieve()
                    .toBodilessEntity()
                    .doOnError(e -> log.warn("Expo push send failed: {}", e.getMessage()))
                    .onErrorResume(e -> reactor.core.publisher.Mono.empty())
                    .block();
            log.info("Sent push notification to {} device(s): {}", expoPushTokens.size(), title);
        } catch (Exception e) {
            log.warn("Expo push send failed: {}", e.getMessage());
        }
    }
}