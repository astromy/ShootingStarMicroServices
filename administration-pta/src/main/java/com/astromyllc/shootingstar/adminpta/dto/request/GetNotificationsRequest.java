package com.astromyllc.shootingstar.adminpta.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetNotificationsRequest {
    private String institutionCode;
    private String recipientContact; // parent's username/contact — used to compute `read` per item
}