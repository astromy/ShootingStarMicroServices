package com.astromyllc.shootingstar.setup.dto.paystack;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionData {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("status")
    private String status;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("message")
    private String message;

    @JsonProperty("gateway_response")
    private String gatewayResponse;

    @JsonProperty("paid_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime paidAt;

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("ip_address")
    private String ipAddress;

    @JsonProperty("metadata")
    private Metadata metadata;

    @JsonProperty("fees_breakdown")
    private FeesBreakdown feesBreakdown;

    @JsonProperty("log")
    private Object log;

    @JsonProperty("fees")
    private Integer fees;

    @JsonProperty("fees_split")
    private Object feesSplit;

    @JsonProperty("authorization")
    private Authorization authorization;

    @JsonProperty("customer")
    private Customer customer;

    @JsonProperty("plan")
    private Object plan;

    @JsonProperty("subaccount")
    private Object subaccount;

    @JsonProperty("split")
    private Object split;

    @JsonProperty("order_id")
    private Object orderId;

    @JsonProperty("paidAt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime paidAtDuplicate;

    @JsonProperty("requested_amount")
    private Integer requestedAmount;

    @JsonProperty("pos_transaction_data")
    private Object posTransactionData;

    @JsonProperty("source")
    private Source source;

}
