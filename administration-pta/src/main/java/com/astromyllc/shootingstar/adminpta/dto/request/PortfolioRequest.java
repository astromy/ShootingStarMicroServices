package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PortfolioRequest {
    private String id;

    @NonNull
    private String studentId;
    @NonNull
    private String institutionCode;

}
