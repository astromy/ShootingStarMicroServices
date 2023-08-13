package com.astromyllc.shootingstar.setup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ApplicationCategoryRequest {
    private Long id;
    private BigDecimal applicationFormAmount;
    private String applicationFormType;
    private int applicationFormQNT;
    private String paymentMedium;
    private String commencement;
    private String closure;
    private int appointmentPerDay;
    private String appointmentCommencement;
    private String appointmentClosure;
}
