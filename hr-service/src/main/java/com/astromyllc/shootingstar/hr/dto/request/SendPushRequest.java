package com.astromyllc.shootingstar.hr.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SendPushRequest {
    private List<String> staffCodes;
    private String title;
    private String body;
}