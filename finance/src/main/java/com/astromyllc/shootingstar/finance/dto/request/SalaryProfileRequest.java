package com.astromyllc.shootingstar.finance.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// institutionCode, staffName, designation and updatedBy are set by the gateway (from the login and HR).
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SalaryProfileRequest {
    private String institutionCode;
    private String staffId;
    private String staffName;
    private String designation;
    private Double basicSalary;
    private String paymentMethod;   // BANK | MOMO | CASH
    private String bankName;
    private String bankBranch;
    private String accountName;
    private String accountNumber;
    private String momoNumber;
    private Boolean active;
    private String updatedBy;
    private List<SalaryItemRequest> items;
}
