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
public class CustomField {

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("variable_name")
    private String variableName;

    @JsonProperty("value")
    private String value;
}