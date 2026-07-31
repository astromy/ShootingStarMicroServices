package com.astromyllc.shootingstar.library.controller;

import com.astromyllc.shootingstar.library.dto.request.CheckoutRequest;
import com.astromyllc.shootingstar.library.dto.request.LoanFetchRequest;
import com.astromyllc.shootingstar.library.dto.request.ReturnRequest;
import com.astromyllc.shootingstar.library.dto.request.StudentLoanFetchRequest;
import com.astromyllc.shootingstar.library.dto.response.LoanResponse;
import com.astromyllc.shootingstar.library.service.LibraryLoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/library/loans")
@RequiredArgsConstructor
@Slf4j
public class LoanController {

    private final LibraryLoanService loanService;

    /** Check a book out to a student — backs Pulse's Library screen (action = CHECKOUT) */
    @PostMapping("/checkout")
    public LoanResponse checkout(@RequestBody CheckoutRequest request) {
        log.info("Checkout: {} → {} at {}", request.getBookCode(), request.getStudentIndex(), request.getInstitutionCode());
        return loanService.checkout(request);
    }

    /** Return a book — backs Pulse's Library screen (action = RETURN) */
    @PostMapping("/return")
    public LoanResponse returnBook(@RequestBody ReturnRequest request) {
        log.info("Return: {} from {} at {}", request.getBookCode(), request.getStudentIndex(), request.getInstitutionCode());
        return loanService.returnBook(request);
    }

    /** All loans for an institution, optionally filtered by status */
    @PostMapping("/get-by-institution")
    public List<LoanResponse> getByInstitution(@RequestBody LoanFetchRequest request) {
        return loanService.getLoansByInstitution(request);
    }

    /** A single student's loan history (current + past) */
    @PostMapping("/get-by-student")
    public List<LoanResponse> getByStudent(@RequestBody StudentLoanFetchRequest request) {
        return loanService.getLoansByStudent(request);
    }

    /** Currently checked-out books for an institution */
    @GetMapping("/active/{institutionCode}")
    public List<LoanResponse> getActive(@PathVariable String institutionCode) {
        return loanService.getActiveLoans(institutionCode);
    }

    /** Active loans past their due date */
    @GetMapping("/overdue/{institutionCode}")
    public List<LoanResponse> getOverdue(@PathVariable String institutionCode) {
        return loanService.getOverdueLoans(institutionCode);
    }

    @GetMapping("/{id}")
    public Optional<LoanResponse> getById(@PathVariable String id) {
        return loanService.getLoanById(id);
    }
}
