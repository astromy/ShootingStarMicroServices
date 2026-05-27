package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class JobDescriptionRequestDetails {
    private Long idJobDescription;
    @NonNull
    private String jobDescription;
}
