package com.astromyllc.shootingstar.adminpta.controller;

import com.astromyllc.shootingstar.adminpta.config.VoiceMessageTooLargeException;
import com.astromyllc.shootingstar.adminpta.dto.request.MarkVoiceMessageListenedRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.VoiceMessageRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageAudioResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageResponse;
import com.astromyllc.shootingstar.adminpta.serviceInterface.VoiceMessageServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VoiceMessageController {

    private final VoiceMessageServiceInterface voiceMessageServiceInterface;

    @PostMapping("/api/administration-pta/sendVoiceMessage")
    public ResponseEntity<?> sendVoiceMessage(@RequestBody VoiceMessageRequest request) {
        try {
            VoiceMessageResponse response = voiceMessageServiceInterface.sendVoiceMessage(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (VoiceMessageTooLargeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/api/administration-pta/getSentVoiceMessages")
    public ResponseEntity<List<VoiceMessageResponse>> getSentVoiceMessages(@RequestBody SingleStringRequest institutionCode) {
        return ResponseEntity.ok(voiceMessageServiceInterface.getVoiceMessagesByInstitution(institutionCode));
    }

    @PostMapping("/api/administration-pta/getVoiceMessageAudio")
    public ResponseEntity<VoiceMessageAudioResponse> getVoiceMessageAudio(@RequestBody SingleStringRequest voiceMessageId) {
        Optional<VoiceMessageAudioResponse> response = voiceMessageServiceInterface.getVoiceMessageAudio(voiceMessageId);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/administration-pta/markVoiceMessageListened")
    public ResponseEntity<Void> markVoiceMessageListened(@RequestBody MarkVoiceMessageListenedRequest request) {
        voiceMessageServiceInterface.markVoiceMessageListened(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/administration-pta/deleteVoiceMessage")
    public ResponseEntity<Void> deleteVoiceMessage(@RequestBody SingleStringRequest voiceMessageId) {
        voiceMessageServiceInterface.deleteVoiceMessage(voiceMessageId);
        return ResponseEntity.ok().build();
    }
}