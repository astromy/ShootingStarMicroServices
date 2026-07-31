package com.astromyllc.shootingstar.hr.service;

import com.astromyllc.shootingstar.hr.dto.request.RegisterPushTokenRequest;
import com.astromyllc.shootingstar.hr.dto.request.SendPushRequest;
import com.astromyllc.shootingstar.hr.model.PushToken;
import com.astromyllc.shootingstar.hr.repository.PushTokenRepository;
import com.astromyllc.shootingstar.hr.serviceInterface.PushTokenServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushTokenService implements PushTokenServiceInterface {

    private final PushTokenRepository pushTokenRepository;
    private final ExpoPushService expoPushService;

    @Override
    public void registerToken(RegisterPushTokenRequest request) {
        PushToken token = pushTokenRepository.findByStaffCode(request.getStaffCode())
                .orElse(PushToken.builder().staffCode(request.getStaffCode()).build());
        token.setExpoPushToken(request.getExpoPushToken());
        token.setUpdatedAt(Instant.now());
        pushTokenRepository.save(token);
        log.info("Registered push token for staff {}", request.getStaffCode());
    }

    @Override
    public void sendPushToStaff(SendPushRequest request) {
        List<String> tokens = pushTokenRepository.findByStaffCodeIn(request.getStaffCodes())
                .stream()
                .map(PushToken::getExpoPushToken)
                .toList();
        expoPushService.sendPush(tokens, request.getTitle(), request.getBody());
    }
}