package com.astromyllc.shootingstar.library.dto.request;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class BookRequest {
    private String institutionCode;
    private String bookCode;
    private String isbn;
    private String title;
    private String author;
    private String category;      // FICTION | NON_FICTION | TEXTBOOK | REFERENCE | PERIODICAL | OTHER
    private String publisher;
    private Integer totalCopies;
}
