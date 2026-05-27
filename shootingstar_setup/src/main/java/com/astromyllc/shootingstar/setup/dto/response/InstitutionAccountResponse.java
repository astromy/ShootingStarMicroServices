package com.astromyllc.shootingstar.setup.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InstitutionAccountResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idInstitutionAccount;
    @NonNull
    private String institutionCode;
    private String activationState;
    private String activationDate;
}
