package com.astromyllc.shootingstar.accommodation.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "ticket")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTicket;

    @NonNull
    private String institutionCode;

    @NonNull
    private Long idBlock;

    // Optional - some issues are block-wide (e.g. a leaking roof), not
    // specific to one room.
    private Long idBlockRoom;

    @NonNull
    private String title;
    private String description;

    private String raisedByStaffId;
    private String raisedByStaffName;

    // OPEN, IN_PROGRESS, RESOLVED
    @Builder.Default
    private String status = "OPEN";

    // LOW, MEDIUM, HIGH
    @Builder.Default
    private String priority = "MEDIUM";

    @Builder.Default
    private LocalDate dateRaised = LocalDate.now();
    private LocalDate dateResolved;
    private String resolutionNotes;
}
