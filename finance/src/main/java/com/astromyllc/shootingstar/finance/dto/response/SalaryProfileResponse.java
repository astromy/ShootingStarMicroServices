package com.astromyllc.shootingstar.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryProfileResponse {
    private Long salaryProfileId;
    private String staffId;
    private String staffName;
    private String designation;
    private Double basicSalary;
    private String paymentMethod;
    private String bankName;
    private String bankBranch;
    private String accountName;
    private String accountNumber;
    private String momoNumber;
    private Boolean active;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private List<SalaryItemResponse> items;
}
