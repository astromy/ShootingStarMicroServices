package com.astromyllc.shootingstar.hr.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "push_token")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class PushToken {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String staffCode;

    private String expoPushToken;
    private Instant updatedAt;
}