package com.astromyllc.shootingstar.library.dto.request;

import lombok.*;

/** Matches the StoreFetchRequest convention used by stores-inventory's get-by-institution. */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class BookFetchRequest {
    private String institutionCode;
}
