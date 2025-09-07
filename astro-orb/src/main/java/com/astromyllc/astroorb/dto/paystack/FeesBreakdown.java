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
public class FeesBreakdown {

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("formula")
    private Object formula;

    @JsonProperty("type")
    private String type;
}