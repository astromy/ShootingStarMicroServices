package com.astromyllc.shootingstar.finance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "financialbooks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class Financial_Books {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long financialBooksId;
    private String book_Name;
    private LocalDateTime creationDate;
    private String bookCategory;
    private String institutionCode;
}
