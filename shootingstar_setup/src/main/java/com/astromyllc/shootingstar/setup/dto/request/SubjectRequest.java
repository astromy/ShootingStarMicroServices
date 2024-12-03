package com.astromyllc.shootingstar.setup.dto.request;

import jdk.dynalink.linker.LinkerServices;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubjectRequest {
    @NonNull
    private String institution;
    private List<SubjectDetails> subjectDetails;
}
