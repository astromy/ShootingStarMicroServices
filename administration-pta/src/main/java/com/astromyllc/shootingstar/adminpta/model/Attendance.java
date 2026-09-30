package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

// One record per student per school day. Marking the same student again on
// the same day updates this record rather than adding a second one — the
// unique (studentId, date) index enforces that at the database level.
@Document(value = "attendance")
@CompoundIndexes({
        @CompoundIndex(name = "student_date_unique", def = "{'studentId': 1, 'date': 1}", unique = true),
        @CompoundIndex(name = "institution_date", def = "{'institutionCode': 1, 'date': 1}")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Attendance {
    @Id
    private ObjectId id;

    private String studentId;
    private String institutionCode;

    // Snapshot of the student's class on that day, so history stays correct
    // after promotions.
    private String studentClass;

    // ISO "YYYY-MM-DD". Stored as a string so date-range queries compare
    // lexically, with no timezone shifting.
    private String date;

    // "PRESENT" | "LATE" | "ABSENT"
    private String status;

    // staffCode of whoever marked it (optional).
    private String recordedBy;

    // Set server-side on every create/update, never trusted from the client.
    private Instant updatedAt;
}
