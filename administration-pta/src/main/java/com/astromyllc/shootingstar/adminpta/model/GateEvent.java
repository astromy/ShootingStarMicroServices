package com.astromyllc.shootingstar.adminpta.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(value = "gate_event")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class GateEvent {
    @Id
    private ObjectId id;

    @Indexed
    private String studentId;

    @Indexed
    private String institutionCode;

    // staffCode of the guard who scanned the student in/out
    private String recordedBy;

    // "IN" or "OUT"
    private String type;

    // Set server-side at insert time, never trusted from the client.
    private Instant timestamp;
}
