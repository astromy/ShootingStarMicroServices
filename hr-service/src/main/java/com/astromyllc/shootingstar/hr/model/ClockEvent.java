package com.astromyllc.shootingstar.hr.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "clock_event")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class ClockEvent {
    @Id
    private ObjectId id;

    @Indexed
    private String staffCode;

    @Indexed
    private String institutionCode;

    // "IN" or "OUT"
    private String type;

    private double latitude;
    private double longitude;

    // Set server-side at insert time — never trust a client-supplied timestamp
    // for something attendance/pay-adjacent.
    private Instant timestamp;
}