package com.astromyllc.shootingstar.finance.dto.request;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
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
@Embeddable
public class BillingsRequest {
    private Long billingId;
    @Nonnull
    private String term;
    @Nonnull
    private String studentClass;
    @Nonnull
    private List<String> studentId;
    @Nonnull
    private List<String> billname;
    @Nonnull
    private String institutionCode;
}
