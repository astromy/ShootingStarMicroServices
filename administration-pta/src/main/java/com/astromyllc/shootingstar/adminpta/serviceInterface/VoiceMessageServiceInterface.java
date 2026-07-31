package com.astromyllc.shootingstar.adminpta.serviceInterface;

import com.astromyllc.shootingstar.adminpta.config.VoiceMessageTooLargeException;
import com.astromyllc.shootingstar.adminpta.dto.request.MarkVoiceMessageListenedRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.VoiceMessageRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageAudioResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageResponse;

import java.util.List;
import java.util.Optional;

public interface VoiceMessageServiceInterface {
    VoiceMessageResponse sendVoiceMessage(VoiceMessageRequest request) throws VoiceMessageTooLargeException;

    // Metadata only — no audio bytes. Serves both the sender's own "what
    // have I sent" history and (once Academix is wired up) a recipient's
    // inbox view; this service doesn't currently filter by recipient the
    // same way targetClassIds implies it eventually should.
    List<VoiceMessageResponse> getVoiceMessagesByInstitution(SingleStringRequest institutionCode);

    // The one place actual audio bytes travel — fetched per-message, only
    // when something is about to be played.
    Optional<VoiceMessageAudioResponse> getVoiceMessageAudio(SingleStringRequest voiceMessageId);

    void markVoiceMessageListened(MarkVoiceMessageListenedRequest request);

    void deleteVoiceMessage(SingleStringRequest voiceMessageId);
}