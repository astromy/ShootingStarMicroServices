package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;

// Deliberately has no audioBase64 field — this is what getSentVoiceMessages
// returns for a whole list, and a list of these should stay cheap to fetch
// and hold in memory regardless of how many voice notes exist. Actual audio
// bytes are fetched one at a time via getVoiceMessageAudio, only when a
// specific note is played.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VoiceMessageResponse {
    private String id;
    private String institutionCode;
    private String sentBy;
    private String title;
    private List<Long> targetClassIds;
    private String mimeType;
    private int durationSeconds;
    private long sizeBytes;
    private Instant timestamp;
    private Set<String> listenedByContacts;
}