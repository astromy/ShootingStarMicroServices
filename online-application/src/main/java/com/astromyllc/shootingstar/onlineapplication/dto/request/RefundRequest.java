package com.astromyllc.shootingstar.onlineapplication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {
    private String reference;
    private Long amount; // in pesewas — null means full refund
    private String reason;
}