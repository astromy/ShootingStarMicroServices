package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(value = "announcement")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Announcement {
    @Id
    private ObjectId id;

    @Indexed
    private String institutionCode;

    private String title;
    private String message;
    private String sentBy;

    private String priority;
    private List<Long> targetClassIds;
    private Instant timestamp;
}