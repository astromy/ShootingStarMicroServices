package com.astromyllc.shootingstar.academics.dto.alien;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubjectRequest {
    private Long id;
    private String name;
    private String classGroup;
    private int preference;
}
