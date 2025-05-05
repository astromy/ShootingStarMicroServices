package com.astromyllc.shootingstar.hr.dto.response;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class StaffSubjectsResponse {
        private ObjectId id;
        private String subject;
        private String subjectClass;
        private String staffId;
        private String institutionCode;
    }


