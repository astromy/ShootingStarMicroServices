package com.astromyllc.shootingstar.onlineapplication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClassesRequest {
    private Long id;
    private String name;
    private String classGroup;
}
