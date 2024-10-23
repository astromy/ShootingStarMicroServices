package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class BillingFetchRequest {
    private String institutionCode;
    private String studentId;
    private String studentClass;
    private String term;
}
