package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClassGroupRequest {
    @NonNull
    private String institution;
    @NonNull
    private String classGroup;
}
