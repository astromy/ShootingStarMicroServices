package com.astromyllc.shootingstar.setup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PromotionsResponse {
    Long promotionId;
    String currentClass;
    String targetClass;
    String academicYear;
    LocalDate transactionDate;
}
