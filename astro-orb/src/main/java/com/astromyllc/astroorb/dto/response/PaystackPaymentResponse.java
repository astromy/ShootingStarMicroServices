package com.astromyllc.astroorb.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PaystackPaymentResponse {
    private String event;
    private PaystackData data;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class PaystackData {
        private Long id;
        private Integer amount;
        private String currency;
        private String transactionDate;
        private String status;
        private String reference;
        private String domain;
        private String gatewayResponse;
        private String channel;
        private String ipAddress;
        private Metadata metadata;
        private Customer customer;
        private Authorization authorization;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class Metadata {
        private List<CustomField> customFields;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class CustomField {
        private String displayName;
        private String variableName;
        private String value;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class Customer {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public static class Authorization {
        private String authorizationCode;
        private String bin;
        private String last4;
        private String expMonth;
        private String expYear;
        private String channel;
        private String cardType;
    }
}
