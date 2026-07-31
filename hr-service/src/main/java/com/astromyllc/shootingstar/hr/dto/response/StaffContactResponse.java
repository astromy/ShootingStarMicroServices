package com.astromyllc.shootingstar.hr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StaffContactResponse {
    private String staffCode;
    private String staffEmail;
    private String designation;
}