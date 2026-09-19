package com.astromyllc.shootingstar.setup.config;

import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.InstitutionAccount;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.subscription.SubscriptionPlan;
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
import java.util.stream.Collectors;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class Cron {
    private final InstitutionUtils institutionUtils;
    private final InstitutionAccountUtil institutionAccountUtils;
    private final InstitutionRepository institutionRepository;

    //@Scheduled(cron = "0 */3 * * * ?")
    @Scheduled(cron = "0 1 0 * * ?")
    public void updateInstitutionStatus() {
        LocalDate currentDate = LocalDate.now();
        // Pre-compute the payment period (Sept previous year to Aug current year)
        LocalDate paymentStart = LocalDate.of(currentDate.getYear() - 1, Month.SEPTEMBER, 1);
        LocalDate paymentEnd = LocalDate.of(currentDate.getYear(), Month.AUGUST, 31);


        // Early-renewal window: Sept 1 this year -> Jan 1 next year. The
        // September suspend check runs right as a new cycle opens, so an
        // institution that renews right at (or shortly after) that point - like
        // institution 00147, whose only payments are Sept 9-14, 2026, entirely
        // after the primary window's Aug 31 cutoff - needs that payment
        // recognized immediately rather than waiting for next year's window.
        LocalDate earlyRenewalStart = LocalDate.of(currentDate.getYear(), Month.SEPTEMBER, 1);
        LocalDate earlyRenewalEnd = LocalDate.of(currentDate.getYear() + 1, Month.JANUARY, 1);

        // Create lookup map for faster institution account access
        Map<String, List<InstitutionAccount>> accountsByInstitution = InstitutionAccountUtil.institutionAccountsGlobalList.stream()
                .collect(Collectors.groupingBy(InstitutionAccount::getInstitutionCode));

        InstitutionUtils.institutionGlobalList.forEach(institution -> {
            String beceCode = institution.getBececode();
            List<InstitutionAccount> accounts = accountsByInstitution.get(beceCode);

            boolean hasPayment = accounts != null && accounts.stream()
                    .anyMatch(account -> isWithinPaymentPeriod(account.getActivationDate(), paymentStart, paymentEnd)
                            || isWithinPaymentPeriod(account.getActivationDate(), earlyRenewalStart, earlyRenewalEnd));

            if (!hasPayment && shouldSuspend(institution, currentDate)) {
                institution.setStatus("suspended");
                institutionRepository.save(institution);
            }
        });
    }

    private boolean isWithinPaymentPeriod(String activationDate, LocalDate start, LocalDate end) {

        try {
            return isWithinRange(LocalDate.parse(activationDate), start, end);
        } catch (Exception ignored) {
            // fall through to LocalDateTime parsing below
        }
        try {
            return isWithinRange(java.time.LocalDateTime.parse(activationDate).toLocalDate(), start, end);
        } catch (Exception e) {
            return false; // Handle invalid dates as non-payment
        }
    }

    private boolean isWithinRange(LocalDate date, LocalDate start, LocalDate end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    private boolean shouldSuspend(Institution institution, LocalDate currentDate) {
        LocalDate creationDate = institution.getCreationDate();
        Month currentMonth = currentDate.getMonth();

        SubscriptionPlan plan = SubscriptionPlan.fromLabel(institution.getSubscription());
        if (plan == SubscriptionPlan.STARTER) {
            return false;
        }

        return ((currentMonth == Month.SEPTEMBER && creationDate.isBefore(currentDate.minusMonths(5)))
                || (currentMonth == Month.MARCH));
    }

}
