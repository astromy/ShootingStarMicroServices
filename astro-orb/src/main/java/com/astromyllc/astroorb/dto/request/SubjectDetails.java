package com.astromyllc.astroorb.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubjectDetails {

    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String classGroup;
    private int preference;
}
