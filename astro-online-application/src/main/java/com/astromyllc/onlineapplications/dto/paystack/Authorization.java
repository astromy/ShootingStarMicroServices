package com.astromyllc.onlineapplications.dto.paystack;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authorization {

    @JsonProperty("authorization_code")
    private String authorizationCode;

    @JsonProperty("bin")
    private String bin;

    @JsonProperty("last4")
    private String last4;

    @JsonProperty("exp_month")
    private String expMonth;

    @JsonProperty("exp_year")
    private String expYear;

    @JsonProperty("channel")
    private String channel;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("bank")
    private String bank;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("brand")
    private String brand;

    @JsonProperty("reusable")
    private Boolean reusable;

    @JsonProperty("signature")
    private Object signature;

    @JsonProperty("account_name")
    private Object accountName;

    @JsonProperty("receiver_bank_account_number")
    private Object receiverBankAccountNumber;

    @JsonProperty("receiver_bank")
    private Object receiverBank;
}
