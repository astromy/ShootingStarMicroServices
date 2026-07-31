package com.astromyllc.shootingstar.library.controller;

import com.astromyllc.shootingstar.library.dto.request.BookFetchRequest;
import com.astromyllc.shootingstar.library.dto.request.BookRequest;
import com.astromyllc.shootingstar.library.dto.request.BookSearchRequest;
import com.astromyllc.shootingstar.library.dto.response.BookResponse;
import com.astromyllc.shootingstar.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Follows the same class-level-base-path + verb-per-action style as
 * stores-inventory's StoreItemController (one of the two house-style
 * exceptions to "full path on the method").
 */
@RestController
@RequestMapping("/api/library/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    /** Admin: add a new title to the catalogue */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@RequestBody BookRequest request) {
        log.info("Create book: {} for {}", request.getTitle(), request.getInstitutionCode());
        return bookService.createBook(request);
    }

    /** Admin: update book details */
    @PutMapping("/{id}")
    public BookResponse updateBook(@PathVariable String id, @RequestBody BookRequest request) {
        return bookService.updateBook(id, request);
    }

    /** Admin: update book details — POST alias for fetchPost-based frontends (matches stores' convention) */
    @PostMapping("/update/{id}")
    public BookResponse updateBookPost(@PathVariable String id, @RequestBody BookRequest request) {
        return bookService.updateBook(id, request);
    }

    /** Admin: soft-delete a title */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateBook(@PathVariable String id) {
        bookService.deactivateBook(id);
    }

    /** Admin: soft-delete a title — POST alias */
    @PostMapping("/deactivate/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateBookPost(@PathVariable String id) {
        bookService.deactivateBook(id);
    }

    /** Full catalogue for an institution (admin/catalogue view) */
    @PostMapping("/get-by-institution")
    public List<BookResponse> getByInstitution(@RequestBody BookFetchRequest request) {
        return bookService.getBooksByInstitution(request);
    }

    /** Free-text search across title + author */
    @PostMapping("/search")
    public List<BookResponse> search(@RequestBody BookSearchRequest request) {
        return bookService.searchBooks(request);
    }

    /** Scan-or-type lookup by the institution's own book code — backs Pulse's Library screen */
    @GetMapping("/code/{institutionCode}/{bookCode}")
    public Optional<BookResponse> getByCode(@PathVariable String institutionCode, @PathVariable String bookCode) {
        return bookService.getBookByCode(institutionCode, bookCode);
    }

    @GetMapping("/{id}")
    public Optional<BookResponse> getById(@PathVariable String id) {
        return bookService.getBookById(id);
    }
}
