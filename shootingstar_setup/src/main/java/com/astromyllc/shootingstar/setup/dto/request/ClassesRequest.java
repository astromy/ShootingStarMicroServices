package com.astromyllc.shootingstar.setup.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ClassesRequest {
    @NonNull
    String institution;
    List<ClassDetail> classDetailList;
}
