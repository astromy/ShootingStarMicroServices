package com.astromyllc.shootingstar.library.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * A single checkout/return transaction against a Book.
 *
 * Lifecycle: ACTIVE → RETURNED
 *            ACTIVE → LOST   (manual admin action, not modelled as an endpoint yet —
 *                              add a markLost endpoint if/when that workflow is needed)
 *
 * "Overdue" is intentionally NOT a stored status transition — it's computed at query
 * time (status == ACTIVE && dueDate is before now) via LibraryLoanRepository /
 * LoanResponse.overdue, the same way stores-inventory computes StoreItemResponse.lowStock
 * rather than mutating stock records. Avoids needing a scheduled job just to flip a flag.
 *
 * studentName / bookTitle are denormalized onto the loan at checkout time so loan
 * history displays correctly even if the student record or book title changes later —
 * same reasoning as StoreOrder denormalizing buyerName/buyerContact.
 */
@Document(collection = "library_loans")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class LibraryLoan {

    @Id
    private String id;

    @Indexed
    private String institutionCode;

    @Indexed
    private String bookCode;
    private String bookTitle;

    @Indexed
    private String studentIndex;
    private String studentName;

    /** Staff member (or Pulse device user) who processed the checkout/return */
    private String processedBy;

    private String status;            // ACTIVE | RETURNED | LOST

    private LocalDateTime checkoutDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnedDate;

    private String notes;
}
