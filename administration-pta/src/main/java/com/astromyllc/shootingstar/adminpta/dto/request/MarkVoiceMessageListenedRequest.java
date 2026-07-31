package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MarkVoiceMessageListenedRequest {
    private String voiceMessageId;
    private String recipientContact; // parent contact / student ID marking it listened
}