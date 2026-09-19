package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * A notification targeted at one specific student's parent(s) — as opposed
 * to Announcement, which is institution/class-wide. First consumer is the
 * clinic module (kind "CLINIC", category "HEALTH_VISIT"), but the shape is
 * generic so any other module can post the same kind of event later.
 */
@Document(value = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Notification {
    @Id
    private ObjectId id;

    @Indexed
    private String institutionCode;

    @Indexed
    private String studentId;

    private String kind;           // e.g. "CLINIC"
    private String category;       // e.g. "HEALTH_VISIT"
    private String sourceModule;   // e.g. "clinic"

    private String title;
    private String message;
    private String sentBy;

    private Instant timestamp;
    private boolean read;
}
