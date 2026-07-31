package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.config.VoiceMessageTooLargeException;
import com.astromyllc.shootingstar.adminpta.dto.request.VoiceMessageRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageAudioResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageResponse;
import com.astromyllc.shootingstar.adminpta.model.VoiceMessage;

import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;

public class VoiceMessageUtil {

    // Backstop against a misbehaving/compromised client — not the primary
    // size control (that's the mobile recorder's compression settings),
    // just a hard ceiling so nothing absurd ever gets persisted regardless
    // of what the client claims about itself. At the mobile app's ~24kbps
    // mono AAC recording setting, 120s of audio is roughly 350KB decoded,
    // so 750KB leaves real margin without allowing anything egregious.
    public static final int MAX_DURATION_SECONDS = 120;
    public static final long MAX_SIZE_BYTES = 750 * 1024L;

    public static VoiceMessage mapRequest_ToVoiceMessage(VoiceMessageRequest request) {
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(request.getAudioBase64());
        } catch (IllegalArgumentException e) {
            throw new VoiceMessageTooLargeException("Audio data is not valid base64.");
        }

        if (request.getDurationSeconds() > MAX_DURATION_SECONDS) {
            throw new VoiceMessageTooLargeException(
                    "Voice notes are limited to " + MAX_DURATION_SECONDS + " seconds.");
        }
        if (decoded.length > MAX_SIZE_BYTES) {
            throw new VoiceMessageTooLargeException(
                    "This recording is too large (" + (decoded.length / 1024) + "KB). Please re-record.");
        }

        return VoiceMessage.builder()
                .institutionCode(request.getInstitutionCode())
                .sentBy(request.getSentBy())
                .title(request.getTitle())
                .targetClassIds(request.getTargetClassIds())
                .audioBase64(request.getAudioBase64())
                .mimeType(request.getMimeType())
                .durationSeconds(request.getDurationSeconds())
                .sizeBytes(decoded.length)
                .listenedByContacts(new HashSet<>())
                .timestamp(Instant.now())
                .build();
    }

    public static VoiceMessageResponse mapVoiceMessage_ToVoiceMessageResponse(VoiceMessage m) {
        return VoiceMessageResponse.builder()
                .id(m.getId().toHexString())
                .institutionCode(m.getInstitutionCode())
                .sentBy(m.getSentBy())
                .title(m.getTitle())
                .targetClassIds(m.getTargetClassIds())
                .mimeType(m.getMimeType())
                .durationSeconds(m.getDurationSeconds())
                .sizeBytes(m.getSizeBytes())
                .timestamp(m.getTimestamp())
                .listenedByContacts(m.getListenedByContacts())
                .build();
    }

    public static VoiceMessageAudioResponse mapVoiceMessage_ToAudioResponse(VoiceMessage m) {
        return VoiceMessageAudioResponse.builder()
                .id(m.getId().toHexString())
                .audioBase64(m.getAudioBase64())
                .mimeType(m.getMimeType())
                .durationSeconds(m.getDurationSeconds())
                .build();
    }
}