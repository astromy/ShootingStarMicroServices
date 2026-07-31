package com.astromyllc.shootingstar.library.dto.request;

import lombok.*;

/** Free-text search across title + author, institution-scoped. */
@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class BookSearchRequest {
    private String institutionCode;
    private String keyword;
}
