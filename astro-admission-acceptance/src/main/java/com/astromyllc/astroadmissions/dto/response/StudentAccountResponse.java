package com.astromyllc.astroadmissions.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StudentAccountResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ObjectId id;
    @NonNull
    private String studentId;
    private String activationState;
    private String activationDate;
}
