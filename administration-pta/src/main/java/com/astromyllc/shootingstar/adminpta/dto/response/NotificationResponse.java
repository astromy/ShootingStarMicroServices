package com.astromyllc.shootingstar.adminpta.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

// One feed item, whether it started life as a text Announcement or a
// VoiceMessage. `kind` tells the client which fields are meaningful:
// "ANNOUNCEMENT" / "EMERGENCY" -> message is populated, audio fields are null.
// "VOICE" -> message is null, durationSeconds/sizeBytes/voiceMessageId are populated
//            (fetch actual audio bytes separately via getVoiceMessageAudio,
//            using voiceMessageId — never inlined here, same reasoning as
//            VoiceMessageResponse).
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class NotificationResponse {
    private String id;
    private String kind; // "ANNOUNCEMENT" | "EMERGENCY" | "VOICE"
    private String institutionCode;
    private String sentBy;
    private String title;
    private List<Long> targetClassIds;
    private Instant timestamp;

    // Text announcements
    private String message;

    // Voice messages
    private String voiceMessageId;
    private String mimeType;
    private Integer durationSeconds;
    private Long sizeBytes;
}
