package com.astromyllc.astropreorder.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
