package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "institution_account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "idInstitutionAccount")
public class InstitutionAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idInstitutionAccount;
    @NonNull
    private String institutionCode;
    private String activationState;
    private String activationDate;
}
