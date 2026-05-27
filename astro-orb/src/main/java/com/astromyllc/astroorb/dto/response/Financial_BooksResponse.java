package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class Financial_BooksResponse {
    private Long financialBooksId;
    private String book_Name;
    private LocalDateTime creationDate;
    private String bookCategory;
    private String institutionCode;
}
