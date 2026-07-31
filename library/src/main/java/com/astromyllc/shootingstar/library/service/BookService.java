package com.astromyllc.shootingstar.library.service;

import com.astromyllc.shootingstar.library.dto.request.BookFetchRequest;
import com.astromyllc.shootingstar.library.dto.request.BookRequest;
import com.astromyllc.shootingstar.library.dto.request.BookSearchRequest;
import com.astromyllc.shootingstar.library.dto.response.BookResponse;

import java.util.List;
import java.util.Optional;

public interface BookService {
    BookResponse createBook(BookRequest request);
    BookResponse updateBook(String id, BookRequest request);
    void deactivateBook(String id);
    List<BookResponse> getBooksByInstitution(BookFetchRequest request);
    List<BookResponse> searchBooks(BookSearchRequest request);
    Optional<BookResponse> getBookByCode(String institutionCode, String bookCode);
    Optional<BookResponse> getBookById(String id);
}
