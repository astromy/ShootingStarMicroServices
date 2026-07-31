package com.astromyllc.shootingstar.hr.serviceInterface;

import com.astromyllc.shootingstar.hr.dto.request.RegisterPushTokenRequest;
import com.astromyllc.shootingstar.hr.dto.request.SendPushRequest;

public interface PushTokenServiceInterface {
    void registerToken(RegisterPushTokenRequest request);

    void sendPushToStaff(SendPushRequest request);
}