package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value="attendance_type")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class AttendanceType {
    @Id
    private ObjectId id;
}
