package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.config.VoiceMessageTooLargeException;
import com.astromyllc.shootingstar.adminpta.dto.request.MarkVoiceMessageListenedRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.adminpta.dto.request.VoiceMessageRequest;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageAudioResponse;
import com.astromyllc.shootingstar.adminpta.dto.response.VoiceMessageResponse;
import com.astromyllc.shootingstar.adminpta.model.VoiceMessage;
import com.astromyllc.shootingstar.adminpta.repository.VoiceMessageRepository;
import com.astromyllc.shootingstar.adminpta.serviceInterface.VoiceMessageServiceInterface;
import com.astromyllc.shootingstar.adminpta.util.VoiceMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceMessageService implements VoiceMessageServiceInterface {

    private final VoiceMessageRepository voiceMessageRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public VoiceMessageResponse sendVoiceMessage(VoiceMessageRequest request) throws VoiceMessageTooLargeException {
        VoiceMessage message = VoiceMessageUtil.mapRequest_ToVoiceMessage(request);
        voiceMessageRepository.save(message);
        log.info("Voice message sent for institution {} by {} ({} bytes, {}s, targeting {})",
                request.getInstitutionCode(), request.getSentBy(), message.getSizeBytes(),
                message.getDurationSeconds(),
                (request.getTargetClassIds() == null || request.getTargetClassIds().isEmpty())
                        ? "all parents" : request.getTargetClassIds());
        return VoiceMessageUtil.mapVoiceMessage_ToVoiceMessageResponse(message);
    }

    @Override
    public List<VoiceMessageResponse> getVoiceMessagesByInstitution(SingleStringRequest institutionCode) {
        return voiceMessageRepository.findByInstitutionCodeOrderByTimestampDesc(institutionCode.getVal())
                .stream()
                .map(VoiceMessageUtil::mapVoiceMessage_ToVoiceMessageResponse)
                .toList();
    }

    @Override
    public Optional<VoiceMessageAudioResponse> getVoiceMessageAudio(SingleStringRequest voiceMessageId) {
        return voiceMessageRepository.findById(voiceMessageId.getVal())
                .map(VoiceMessageUtil::mapVoiceMessage_ToAudioResponse);
    }

    @Override
    public void markVoiceMessageListened(MarkVoiceMessageListenedRequest request) {
        // One atomic update in the database. The message (audio included) is no longer loaded and
        // saved back for each mark, and two parents marking at the same moment cannot overwrite each other.
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("id").is(request.getVoiceMessageId())),
                new Update().addToSet("listenedByContacts", request.getRecipientContact()),
                VoiceMessage.class);
    }

    @Override
    public void deleteVoiceMessage(SingleStringRequest voiceMessageId) {
        voiceMessageRepository.deleteById(voiceMessageId.getVal());
        log.info("Voice message {} deleted", voiceMessageId.getVal());
    }
}