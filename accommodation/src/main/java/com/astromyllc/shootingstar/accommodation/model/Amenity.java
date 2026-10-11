package com.astromyllc.shootingstar.accommodation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "amenity")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAmenity;

    @NonNull
    private String institutionCode;

    @NonNull
    private Long idBlock;

    // Optional - an amenity can belong to a specific room, or to the block
    // generally (e.g. a common-area TV isn't tied to one room).
    private Long idBlockRoom;

    @NonNull
    private String name;

    // GOOD, DAMAGED, UNDER_REPAIR
    @Builder.Default
    private String condition = "GOOD";

    private String notes;
    private LocalDate lastCheckedDate;
}
