package com.astromyllc.shootingstar.library.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class BookResponse {
    private String id;
    private String institutionCode;
    private String bookCode;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private Integer totalCopies;
    private Integer availableCopies;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
