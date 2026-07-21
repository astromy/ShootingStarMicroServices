package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(value = "bus_boarding_event")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class BusBoardingEvent {
    @Id
    private ObjectId id;

    @Indexed
    private String studentId;

    @Indexed
    private String institutionCode;

    // idBus from the setup service's Bus entity — identifies which bus/route
    private Long busId;
    private String busName;

    // staffCode of the conductor/driver performing the scan
    private String recordedBy;

    // "BOARD" or "ALIGHT"
    private String type;

    // Set server-side at insert time, never trusted from the client.
    private Instant timestamp;
}