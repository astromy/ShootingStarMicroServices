package com.astromyllc.astroorb.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClassDetail {
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String classGroup;
}
