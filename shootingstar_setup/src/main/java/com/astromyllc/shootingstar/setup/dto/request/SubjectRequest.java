package com.astromyllc.shootingstar.setup.dto.request;

import jdk.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubjectRequest {
    private String institution;
    private List<SubjectDetails> subjectDetails;
}
