package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StaffDocumentsRequest {
    private String id;
    private String document;
    private String documentType;
    private String documentExtension;
    private String institutionCode;
}
