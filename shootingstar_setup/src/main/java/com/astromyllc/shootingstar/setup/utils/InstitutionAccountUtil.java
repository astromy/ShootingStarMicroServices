package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.InstitutionAccountRequest;
import com.astromyllc.shootingstar.setup.dto.response.InstitutionAccountResponse;
import com.astromyllc.shootingstar.setup.model.InstitutionAccount;
import com.astromyllc.shootingstar.setup.repository.InstitutionAccountRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InstitutionAccountUtil {

    private final InstitutionAccountRepository accountRepository;
    public static List<InstitutionAccount> institutionAccountsGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static InstitutionAccount mapInstitutionAccountRequest_ToInstitutionAccount(InstitutionAccountRequest r, String studentId) {
        return InstitutionAccount.builder()
                .activationState(r.getActivationState())
                .activationDate((LocalDateTime.now()).toString())
                .institutionCode(r.getInstitutionCode())
                .build();
    }

    public void updateInstitutionAccount(InstitutionAccount sa, InstitutionAccountRequest sar, String StudId) {
        sa.setActivationState(sar.getActivationState());
    }

    public void saveAll(List<InstitutionAccount> sa) {
        accountRepository.saveAll(sa);
        institutionAccountsGlobalList.addAll(sa);
    }
    @PostConstruct
    private void fetchAllStudentSubject() {
        institutionAccountsGlobalList = accountRepository.findAll();
        log.info("Global Institution Accounts List populated with {} records", institutionAccountsGlobalList.size());
    }

    public static InstitutionAccountResponse mapInstitutionAccount_ToInstitutionAccountResponse(InstitutionAccount s) {
        return InstitutionAccountResponse.builder()
                .idInstitutionAccount(s.getIdInstitutionAccount())
                .activationState(s.getActivationState())
                .activationDate(s.getActivationState())
                .institutionCode(s.getInstitutionCode())
                .build();
    }
}
