package com.astromyllc.shootingstar.academics.dto.response;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class TimeTableResponse {
    private Long id;
    private String day;
    private String subject;
    private String period;
    private String subjectClass;
    private String institutionCode;
}
