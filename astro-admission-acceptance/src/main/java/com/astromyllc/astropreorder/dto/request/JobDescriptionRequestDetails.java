package com.astromyllc.astropreorder.dto.request;

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
