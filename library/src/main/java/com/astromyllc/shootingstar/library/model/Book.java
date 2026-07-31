package com.astromyllc.shootingstar.library.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * A title held in the institution's library catalogue.
 *
 * category : FICTION | NON_FICTION | TEXTBOOK | REFERENCE | PERIODICAL | OTHER
 *
 * totalCopies tracks how many physical copies the institution owns;
 * availableCopies is decremented on checkout and incremented on return.
 * Mirrors the StoreItem / stock-count pattern in stores-inventory.
 */
@Document(collection = "library_books")
@CompoundIndexes({
    @CompoundIndex(name = "inst_bookcode_idx", def = "{'institutionCode': 1, 'bookCode': 1}", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class Book {

    @Id
    private String id;

    @Indexed
    private String institutionCode;

    private String bookCode;          // human-scannable code (barcode/QR payload)
    private String isbn;              // optional, distinct from bookCode
    private String title;
    private String author;
    private String category;          // FICTION | NON_FICTION | TEXTBOOK | REFERENCE | PERIODICAL | OTHER
    private String publisher;

    private Integer totalCopies;
    private Integer availableCopies;

    /** false = soft-deleted, hidden from catalogue */
    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
