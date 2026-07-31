package com.astromyllc.shootingstar.library.dto.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class StudentLoanFetchRequest {
    private String institutionCode;
    private String studentIndex;
}
