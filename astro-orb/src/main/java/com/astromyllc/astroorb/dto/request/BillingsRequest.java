package com.astromyllc.astroorb.dto.request;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
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
