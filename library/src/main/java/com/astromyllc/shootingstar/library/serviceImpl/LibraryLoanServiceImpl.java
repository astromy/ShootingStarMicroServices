package com.astromyllc.shootingstar.library.serviceImpl;

import com.astromyllc.shootingstar.library.dto.request.CheckoutRequest;
import com.astromyllc.shootingstar.library.dto.request.LoanFetchRequest;
import com.astromyllc.shootingstar.library.dto.request.ReturnRequest;
import com.astromyllc.shootingstar.library.dto.request.StudentLoanFetchRequest;
import com.astromyllc.shootingstar.library.dto.response.LoanResponse;
import com.astromyllc.shootingstar.library.model.Book;
import com.astromyllc.shootingstar.library.model.LibraryLoan;
import com.astromyllc.shootingstar.library.repository.BookRepository;
import com.astromyllc.shootingstar.library.repository.LibraryLoanRepository;
import com.astromyllc.shootingstar.library.service.LibraryLoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryLoanServiceImpl implements LibraryLoanService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_RETURNED = "RETURNED";
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;

    private final LibraryLoanRepository loanRepository;
    private final BookRepository bookRepository;

    @Override
    public LoanResponse checkout(CheckoutRequest req) {
        Book book = bookRepository.findByInstitutionCodeAndBookCode(req.getInstitutionCode(), req.getBookCode())
                .orElseThrow(() -> new RuntimeException(
                        "Book not found for institution " + req.getInstitutionCode() + ": " + req.getBookCode()));

        if (Boolean.FALSE.equals(book.getActive())) {
            throw new RuntimeException("Book is deactivated: " + req.getBookCode());
        }
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies of \"" + book.getTitle() + "\" to check out");
        }

        // Guard against the same student holding two simultaneous active loans on the
        // same title — mirrors the general "don't let a business event double-fire"
        // caution used elsewhere in the platform (e.g. stores' ledger retry pattern).
        loanRepository.findByInstitutionCodeAndBookCodeAndStudentIndexAndStatus(
                req.getInstitutionCode(), req.getBookCode(), req.getStudentIndex(), STATUS_ACTIVE
        ).ifPresent(existing -> {
            throw new RuntimeException("Student already has an active loan for this book");
        });

        LocalDateTime now = LocalDateTime.now();
        int loanDays = (req.getLoanPeriodDays() != null && req.getLoanPeriodDays() > 0)
                ? req.getLoanPeriodDays() : DEFAULT_LOAN_PERIOD_DAYS;

        LibraryLoan loan = LibraryLoan.builder()
                .institutionCode(req.getInstitutionCode())
                .bookCode(book.getBookCode())
                .bookTitle(book.getTitle())
                .studentIndex(req.getStudentIndex())
                .studentName(req.getStudentName())
                .processedBy(req.getProcessedBy())
                .status(STATUS_ACTIVE)
                .checkoutDate(now)
                .dueDate(now.plusDays(loanDays))
                .build();
        LibraryLoan saved = loanRepository.save(loan);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setUpdatedAt(now);
        bookRepository.save(book);

        log.info("Checked out {} to {} (loan {})", book.getBookCode(), req.getStudentIndex(), saved.getId());
        return toResponse(saved);
    }

    @Override
    public LoanResponse returnBook(ReturnRequest req) {
        LibraryLoan loan;

        if (req.getLoanId() != null && !req.getLoanId().isBlank()) {
            loan = loanRepository.findById(req.getLoanId())
                    .orElseThrow(() -> new RuntimeException("Loan not found: " + req.getLoanId()));
        } else {
            loan = loanRepository.findFirstByInstitutionCodeAndBookCodeAndStudentIndexAndStatusOrderByCheckoutDateDesc(
                    req.getInstitutionCode(), req.getBookCode(), req.getStudentIndex(), STATUS_ACTIVE
            ).orElseThrow(() -> new RuntimeException(
                    "No active loan found for " + req.getStudentIndex() + " / " + req.getBookCode()));
        }

        if (!STATUS_ACTIVE.equals(loan.getStatus())) {
            throw new RuntimeException("Loan " + loan.getId() + " is not currently active (status: " + loan.getStatus() + ")");
        }

        LocalDateTime now = LocalDateTime.now();
        loan.setStatus(STATUS_RETURNED);
        loan.setReturnedDate(now);
        if (req.getProcessedBy() != null) {
            loan.setProcessedBy(req.getProcessedBy());
        }
        LibraryLoan saved = loanRepository.save(loan);

        bookRepository.findByInstitutionCodeAndBookCode(loan.getInstitutionCode(), loan.getBookCode())
                .ifPresent(book -> {
                    // Cap at totalCopies in case of any data drift — never over-credit availability.
                    int newAvailable = Math.min(book.getTotalCopies(), book.getAvailableCopies() + 1);
                    book.setAvailableCopies(newAvailable);
                    book.setUpdatedAt(now);
                    bookRepository.save(book);
                });

        log.info("Returned {} from {} (loan {})", loan.getBookCode(), loan.getStudentIndex(), loan.getId());
        return toResponse(saved);
    }

    @Override
    public List<LoanResponse> getLoansByInstitution(LoanFetchRequest request) {
        List<LibraryLoan> loans = (request.getStatus() == null || request.getStatus().isBlank())
                ? loanRepository.findByInstitutionCode(request.getInstitutionCode())
                : loanRepository.findByInstitutionCodeAndStatus(request.getInstitutionCode(), request.getStatus());
        return loans.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> getLoansByStudent(StudentLoanFetchRequest request) {
        return loanRepository.findByInstitutionCodeAndStudentIndex(
                        request.getInstitutionCode(), request.getStudentIndex())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> getActiveLoans(String institutionCode) {
        return loanRepository.findByInstitutionCodeAndStatus(institutionCode, STATUS_ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> getOverdueLoans(String institutionCode) {
        return loanRepository.findByInstitutionCodeAndStatusAndDueDateBefore(
                        institutionCode, STATUS_ACTIVE, LocalDateTime.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public Optional<LoanResponse> getLoanById(String id) {
        return loanRepository.findById(id).map(this::toResponse);
    }

    private LoanResponse toResponse(LibraryLoan l) {
        boolean overdue = STATUS_ACTIVE.equals(l.getStatus())
                && l.getDueDate() != null
                && l.getDueDate().isBefore(LocalDateTime.now());
        return LoanResponse.builder()
                .id(l.getId())
                .institutionCode(l.getInstitutionCode())
                .bookCode(l.getBookCode())
                .bookTitle(l.getBookTitle())
                .studentIndex(l.getStudentIndex())
                .studentName(l.getStudentName())
                .processedBy(l.getProcessedBy())
                .status(l.getStatus())
                .checkoutDate(l.getCheckoutDate())
                .dueDate(l.getDueDate())
                .returnedDate(l.getReturnedDate())
                .notes(l.getNotes())
                .overdue(overdue)
                .build();
    }
}
