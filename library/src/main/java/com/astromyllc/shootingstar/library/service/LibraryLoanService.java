package com.astromyllc.shootingstar.library.service;

import com.astromyllc.shootingstar.library.dto.request.CheckoutRequest;
import com.astromyllc.shootingstar.library.dto.request.LoanFetchRequest;
import com.astromyllc.shootingstar.library.dto.request.ReturnRequest;
import com.astromyllc.shootingstar.library.dto.request.StudentLoanFetchRequest;
import com.astromyllc.shootingstar.library.dto.response.LoanResponse;

import java.util.List;
import java.util.Optional;

public interface LibraryLoanService {
    LoanResponse checkout(CheckoutRequest request);
    LoanResponse returnBook(ReturnRequest request);
    List<LoanResponse> getLoansByInstitution(LoanFetchRequest request);
    List<LoanResponse> getLoansByStudent(StudentLoanFetchRequest request);
    List<LoanResponse> getActiveLoans(String institutionCode);
    List<LoanResponse> getOverdueLoans(String institutionCode);
    Optional<LoanResponse> getLoanById(String id);
}
