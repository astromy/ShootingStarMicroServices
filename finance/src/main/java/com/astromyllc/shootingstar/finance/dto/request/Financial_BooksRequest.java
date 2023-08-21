package com.astromyllc.shootingstar.finance.dto.request;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
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
