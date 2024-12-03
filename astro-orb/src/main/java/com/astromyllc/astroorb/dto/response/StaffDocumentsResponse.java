package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StaffDocumentsResponse {
    private String id;
    private String document;
    private String documentType;
    private String documentExtension;
}
