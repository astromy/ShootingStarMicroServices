package com.astromyllc.shootingstar.setup.config;

import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.InstitutionAccount;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.utils.InstitutionAccountUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class Cron {
    private final InstitutionUtils institutionUtils;
    private final InstitutionAccountUtil institutionAccountUtils;
    private final InstitutionRepository institutionRepository;

   // @Scheduled(cron = "0 */5 * * * ?")
    @Scheduled(cron = "0 1 0 * * ?")
    public void updateInstitutionStatus() {
        LocalDate currentDate= LocalDate.now();
        // Pre-compute the payment period (Sept previous year to Aug current year)
        LocalDate paymentStart = LocalDate.of(currentDate.getYear() - 1, Month.SEPTEMBER, 1);
        LocalDate paymentEnd = LocalDate.of(currentDate.getYear(), Month.AUGUST, 31);

        // Create lookup map for faster institution account access
        Map<String, List<InstitutionAccount>> accountsByInstitution = InstitutionAccountUtil.institutionAccountsGlobalList.stream()
                .collect(Collectors.groupingBy(InstitutionAccount::getInstitutionCode));

        InstitutionUtils.institutionGlobalList.forEach(institution -> {
            String beceCode = institution.getBececode();
            List<InstitutionAccount> accounts = accountsByInstitution.get(beceCode);

            boolean hasPayment = accounts != null && accounts.stream()
                    .anyMatch(account -> isWithinPaymentPeriod(account.getActivationDate(), paymentStart, paymentEnd));

            if (!hasPayment && shouldSuspend(institution, currentDate)) {
                institution.setStatus("suspended");
                institutionRepository.save(institution);
            }
        });
    }

    private boolean isWithinPaymentPeriod(String activationDate, LocalDate start, LocalDate end) {
        try {
            LocalDate date = LocalDate.parse(activationDate);
            return !date.isBefore(start) && !date.isAfter(end);
        } catch (Exception e) {
            return false; // Handle invalid dates as non-payment
        }
    }

    private boolean shouldSuspend(Institution institution, LocalDate currentDate) {
        LocalDate creationDate = institution.getCreationDate();
        Month currentMonth = currentDate.getMonth();

        return (currentMonth == Month.SEPTEMBER && creationDate.isBefore(currentDate.minusMonths(5)))
                || (currentMonth == Month.MARCH);
    }

}
