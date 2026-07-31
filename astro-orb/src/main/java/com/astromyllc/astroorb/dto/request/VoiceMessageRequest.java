package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VoiceMessageRequest {
    private String institutionCode;
    private String sentBy;
    private String title;
    private List<Long> targetClassIds;

    private String audioBase64;
    private String mimeType;
    private int durationSeconds;
}