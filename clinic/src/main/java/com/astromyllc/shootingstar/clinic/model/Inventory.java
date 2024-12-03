package com.astromyllc.shootingstar.clinic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String institutionCode;
}
