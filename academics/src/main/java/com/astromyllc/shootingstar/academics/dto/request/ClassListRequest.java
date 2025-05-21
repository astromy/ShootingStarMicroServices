package com.astromyllc.shootingstar.academics.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClassListRequest {
    private String institutionCode;
    private String studentClass;
}
