package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VoiceMessageAudioResponse {
    private String id;
    private String audioBase64;
    private String mimeType;
    private int durationSeconds;
}