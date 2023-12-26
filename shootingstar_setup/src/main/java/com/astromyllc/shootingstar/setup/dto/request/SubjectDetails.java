package com.astromyllc.shootingstar.setup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubjectDetails {

    private Long id;
    private String name;
    private String classGroup;
    private int preference;
}
