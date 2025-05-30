package com.astromyllc.shootingstar.hr.dto.request;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class StaffDocumentsRequest {
    private String id;
    private String document;
    private String documentType;
    private String documentExtension;
    private String institutionCode;
    private String staffDocs;
}
