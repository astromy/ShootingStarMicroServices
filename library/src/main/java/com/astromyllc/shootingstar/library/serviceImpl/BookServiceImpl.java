package com.astromyllc.shootingstar.library.serviceImpl;

import com.astromyllc.shootingstar.library.dto.request.BookFetchRequest;
import com.astromyllc.shootingstar.library.dto.request.BookRequest;
import com.astromyllc.shootingstar.library.dto.request.BookSearchRequest;
import com.astromyllc.shootingstar.library.dto.response.BookResponse;
import com.astromyllc.shootingstar.library.model.Book;
import com.astromyllc.shootingstar.library.repository.BookRepository;
import com.astromyllc.shootingstar.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookResponse createBook(BookRequest req) {
        int total = req.getTotalCopies() != null ? req.getTotalCopies() : 1;
        Book book = Book.builder()
                .institutionCode(req.getInstitutionCode())
                .bookCode(req.getBookCode())
                .isbn(req.getIsbn())
                .title(req.getTitle())
                .author(req.getAuthor())
                .category(req.getCategory())
                .publisher(req.getPublisher())
                .totalCopies(total)
                .availableCopies(total)   // all copies start available
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        log.info("Create book: {} ({}) for {}", req.getTitle(), req.getBookCode(), req.getInstitutionCode());
        return toResponse(bookRepository.save(book));
    }

    @Override
    public BookResponse updateBook(String id, BookRequest req) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));

        book.setTitle(req.getTitle());
        book.setAuthor(req.getAuthor());
        book.setIsbn(req.getIsbn());
        book.setCategory(req.getCategory());
        book.setPublisher(req.getPublisher());

        // Adjust availableCopies proportionally if totalCopies changed (e.g. institution
        // bought 2 more copies, or wrote some off) rather than blindly overwriting it —
        // otherwise editing a book's metadata could silently un-do in-flight loans.
        if (req.getTotalCopies() != null && !req.getTotalCopies().equals(book.getTotalCopies())) {
            int onLoan = book.getTotalCopies() - book.getAvailableCopies();
            int newAvailable = Math.max(0, req.getTotalCopies() - onLoan);
            book.setTotalCopies(req.getTotalCopies());
            book.setAvailableCopies(newAvailable);
        }

        book.setUpdatedAt(LocalDateTime.now());
        return toResponse(bookRepository.save(book));
    }

    @Override
    public void deactivateBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        book.setActive(false);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.save(book);
    }

    @Override
    public List<BookResponse> getBooksByInstitution(BookFetchRequest request) {
        return bookRepository.findByInstitutionCodeAndActiveTrue(request.getInstitutionCode())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookResponse> searchBooks(BookSearchRequest request) {
        String kw = request.getKeyword() == null ? "" : request.getKeyword().trim();
        if (kw.isEmpty()) {
            return getBooksByInstitution(
                    BookFetchRequest.builder().institutionCode(request.getInstitutionCode()).build());
        }
        // Union of title-matches and author-matches, de-duplicated, preserving order.
        var byTitle = bookRepository.findByInstitutionCodeAndActiveTrueAndTitleContainingIgnoreCase(
                request.getInstitutionCode(), kw);
        var byAuthor = bookRepository.findByInstitutionCodeAndActiveTrueAndAuthorContainingIgnoreCase(
                request.getInstitutionCode(), kw);

        LinkedHashSet<Book> merged = new LinkedHashSet<>(byTitle);
        merged.addAll(byAuthor);
        return merged.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public Optional<BookResponse> getBookByCode(String institutionCode, String bookCode) {
        return bookRepository.findByInstitutionCodeAndBookCode(institutionCode, bookCode)
                .map(this::toResponse);
    }

    @Override
    public Optional<BookResponse> getBookById(String id) {
        return bookRepository.findById(id).map(this::toResponse);
    }

    private BookResponse toResponse(Book b) {
        return BookResponse.builder()
                .id(b.getId())
                .institutionCode(b.getInstitutionCode())
                .bookCode(b.getBookCode())
                .isbn(b.getIsbn())
                .title(b.getTitle())
                .author(b.getAuthor())
                .category(b.getCategory())
                .publisher(b.getPublisher())
                .totalCopies(b.getTotalCopies())
                .availableCopies(b.getAvailableCopies())
                .active(b.getActive())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
