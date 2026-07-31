package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(value = "voice_message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class VoiceMessage {
    @Id
    private ObjectId id;

    @Indexed
    private String institutionCode;

    private String sentBy;
    private String title;

    // Empty/null = broadcast to every parent, same convention as Announcement.
    private List<Long> targetClassIds;

    // The actual compressed audio, base64-encoded — same inline-storage
    // pattern as Students.picture. Kept out of VoiceMessageResponse (the
    // list/metadata DTO) entirely; only served through the dedicated
    // getVoiceMessageAudio lookup, so listing 50 messages never pulls 50
    // audio blobs into memory or over the wire.
    private String audioBase64;
    private String mimeType;     // e.g. "audio/aac" or "audio/m4a"
    private int durationSeconds;
    private long sizeBytes;      // decoded byte size, for display/auditing

    @Builder.Default
    private Set<String> listenedByContacts = new HashSet<>();

    private Instant timestamp;
}