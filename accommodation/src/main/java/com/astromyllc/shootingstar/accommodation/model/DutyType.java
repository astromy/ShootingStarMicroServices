package com.astromyllc.shootingstar.accommodation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "duty_type")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DutyType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDutyType;

    @NonNull
    private String institutionCode;

    @NonNull
    private String name;

    private String description;
}
