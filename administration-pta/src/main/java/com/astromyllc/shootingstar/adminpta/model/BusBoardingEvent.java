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

    private Long busId;
    private String busName;
    private Long routeId;
    private String routeName;

    // staffCode of the conductor/driver performing the scan
    private String recordedBy;

    // "BOARD" or "ALIGHT"
    private String type;

    private boolean routeMismatch;
    private String expectedRouteName;

    private Instant timestamp;
}