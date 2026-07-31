package com.astromyllc.shootingstar.library.repository;

import com.astromyllc.shootingstar.library.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findByInstitutionCodeAndActiveTrue(String institutionCode);

    Optional<Book> findByInstitutionCodeAndBookCode(String institutionCode, String bookCode);

    List<Book> findByInstitutionCodeAndActiveTrueAndTitleContainingIgnoreCase(
            String institutionCode, String titleKeyword);

    List<Book> findByInstitutionCodeAndActiveTrueAndAuthorContainingIgnoreCase(
            String institutionCode, String authorKeyword);
}
