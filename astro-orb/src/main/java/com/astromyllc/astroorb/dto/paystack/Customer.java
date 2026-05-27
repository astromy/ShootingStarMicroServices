package com.astromyllc.astroorb.dto.paystack;

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
public class Customer {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("customer_code")
    private String customerCode;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("metadata")
    private Object metadata;

    @JsonProperty("risk_action")
    private String riskAction;

    @JsonProperty("international_format_phone")
    private Object internationalFormatPhone;
}
