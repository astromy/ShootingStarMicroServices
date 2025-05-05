package com.astromyllc.shootingstar.hr.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StaffSubjectsRequest {
    private ObjectId id;
    private String subject;
    private String subjectClass;
    private String staffId;
    private String institutionCode;
}
