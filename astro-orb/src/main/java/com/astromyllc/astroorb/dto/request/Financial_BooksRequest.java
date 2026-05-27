package com.astromyllc.astroorb.dto.request;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class Financial_BooksRequest {
    private Long financialBooksId;
    @Nonnull
    private String book_Name;
    private String creationDate;
    @Nonnull
    private String bookCategory;
    @Nonnull
    private String institutionCode;
}
