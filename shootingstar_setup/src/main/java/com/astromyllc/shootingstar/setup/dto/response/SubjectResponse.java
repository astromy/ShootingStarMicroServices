package com.astromyllc.shootingstar.setup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubjectResponse {
    private Long id;
    private String name;
    private String classGroup;
    private String classGroupName;
    private int preference;
}
