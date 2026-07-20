package com.astromyllc.shootingstar.storesinventory.config;

import com.astromyllc.shootingstar.storesinventory.event.LedgerPostRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

/**
 * Thin HTTP client that calls the Finance microservice to post
 * a ledger entry whenever a StoreOrder is fulfilled.
 *
 * If the call fails the caller must set ledgerPosted=false and
 * retry later via StoreOrderService.retryFailedLedgerPosts().
 */
@Component
@Slf4j
public class FinanceLedgerClient {

    private final WebClient webClient;

    public FinanceLedgerClient(
            @Value("${gateway.host}") String gatewayHost,
            WebClient.Builder builder) {
        this.webClient = builder.baseUrl(gatewayHost).build();
    }

    /**
     * POST /api/finance/ledger/store-sale
     * Returns the ledger reference string on success, empty on failure.
     */
    public Optional<String> postStoreSaleLedger(LedgerPostRequest payload) {
        try {
            Map<?, ?> result = webClient.post()
                    .uri("/api/finance/ledger/store-sale")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (result != null && result.get("ledgerReference") != null) {
                String ref = result.get("ledgerReference").toString();
                log.info("Ledger posted for order {} → ref {}", payload.getOrderRef(), ref);
                return Optional.of(ref);
            }
        } catch (Exception ex) {
            log.error("Failed to post ledger for order {}: {}", payload.getOrderRef(), ex.getMessage());
        }
        return Optional.empty();
    }
}
