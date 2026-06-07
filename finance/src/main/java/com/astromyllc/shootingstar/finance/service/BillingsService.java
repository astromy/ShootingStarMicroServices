package com.astromyllc.shootingstar.finance.service;

import com.astromyllc.shootingstar.finance.dto.request.BillingFetchRequest;
import com.astromyllc.shootingstar.finance.dto.request.BillingsRequest;
import com.astromyllc.shootingstar.finance.dto.response.BillingsResponse;
import com.astromyllc.shootingstar.finance.dto.response.Student_BillResponse;
import com.astromyllc.shootingstar.finance.model.Billings;
import com.astromyllc.shootingstar.finance.repositoy.BillingsRepository;
import com.astromyllc.shootingstar.finance.serviceInterface.BillingsServiceInterface;
import com.astromyllc.shootingstar.finance.utils.BillUtil;
import com.astromyllc.shootingstar.finance.utils.BillingsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BillingsService implements BillingsServiceInterface {

    private final BillingsRepository billingsRepository;
    private final BillingsUtil billingsUtil;
    private final Student_BillService student_BillService;

    @Override
    public BillingsResponse updateBilling(BillingsRequest r) {
        return null;
    }

    /**
     * Billing flow per the product owner's definition:
     * 1. Cross-join studentId list × selected bill names → individual Billings rows.
     * 2. Persist all rows, add to global list.
     * 3. Group by studentId → sum billamnt per student.
     * 4. For each student, call Student_BillService to create or update Student_Bill:
     * - New student:   amountDue = batch total, oldBalance = 0
     * - Existing:      oldBalance = current amountDue (snapshot),
     * amountDue += batch total, balance recalculated
     * 5. Return the updated Student_Bill records.
     */
    @Override
    public Optional<List<Student_BillResponse>> createBillings(BillingsRequest r) {

        List<Billings> billings = r.getStudentId().stream()
                .flatMap(studentId -> BillUtil.billGlobalList.stream()
                        .filter(bill ->
                                bill.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()) &&
                                        r.getBillname().stream()
                                                .anyMatch(name -> name.equalsIgnoreCase(bill.getBill_Name()))
                        )
                        .map(bill -> billingsUtil.mapBillingRequest_ToBilling(
                                r.getInstitutionCode(),
                                r.getStudentClass(),
                                r.getTerm(),
                                r.getAcademicYear(),      // ← now mapped
                                bill.getBill_Amount(),
                                bill.getBill_Amount(),
                                bill.getBill_Description(),
                                bill.getBill_Name(),
                                studentId
                        ))
                )
                .collect(Collectors.toList());

        billingsRepository.saveAll(billings);
        BillingsUtil.billingGlobalList.addAll(billings);

        // Group by student → sum → produce one Student_BillRequest per student
        List<Student_BillResponse> results = student_BillService.createStudentsBill(
                billings.stream()
                        .collect(Collectors.groupingBy(Billings::getStudentId))
                        .entrySet().stream()
                        .map(entry -> {
                            String studentId = entry.getKey();
                            Billings first = entry.getValue().get(0);
                            double batchTotal = entry.getValue().stream()
                                    .mapToDouble(Billings::getBillamnt).sum();

                            Billings summary = new Billings();
                            summary.setStudentId(studentId);
                            summary.setBillamnt(batchTotal);
                            summary.setBillamntbal(batchTotal);
                            summary.setTerm(first.getTerm());
                            summary.setAcademicYear(first.getAcademicYear());
                            summary.setStudentClass(first.getStudentClass());
                            summary.setInstitutionCode(first.getInstitutionCode());

                            return billingsUtil.mapBilling_ToStudentBill(summary);
                        })
                        .collect(Collectors.toList())
        );

        return Optional.of(results);
    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingsByInstitution(BillingFetchRequest r) {
        return Optional.of(BillingsUtil.billingGlobalList.stream()
                .filter(b -> b.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode()))
                .map(billingsUtil::mapBillings_ToBillingResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingByInstitutionAndStudent(BillingFetchRequest r) {
        return Optional.of(BillingsUtil.billingGlobalList.stream()
                .filter(b -> b.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode())
                        && b.getStudentId().equalsIgnoreCase(r.getStudentId()))
                .map(billingsUtil::mapBillings_ToBillingResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingByInstitutionStudentClassTerm(BillingFetchRequest r) {
        return Optional.of(BillingsUtil.billingGlobalList.stream()
                .filter(b -> b.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode())
                        && b.getStudentId().equalsIgnoreCase(r.getStudentId())
                        && b.getStudentClass().equalsIgnoreCase(r.getStudentClass())
                        && b.getTerm().equalsIgnoreCase(r.getTerm())
                        && (r.getAcademicYear() == null || b.getAcademicYear() == null
                        || b.getAcademicYear().equalsIgnoreCase(r.getAcademicYear())))
                .map(billingsUtil::mapBillings_ToBillingResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchClassBillingByInstitution(BillingFetchRequest r) {
        return Optional.of(BillingsUtil.billingGlobalList.stream()
                .filter(b -> b.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode())
                        && b.getStudentClass().equalsIgnoreCase(r.getStudentClass())
                        && b.getTerm().equalsIgnoreCase(r.getTerm())
                        && (r.getAcademicYear() == null || b.getAcademicYear() == null
                        || b.getAcademicYear().equalsIgnoreCase(r.getAcademicYear())))
                .map(billingsUtil::mapBillings_ToBillingResponse)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchSchoolBillingByInstitution(BillingFetchRequest r) {
        return Optional.of(BillingsUtil.billingGlobalList.stream()
                .filter(b -> b.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode())
                        && b.getTerm().equalsIgnoreCase(r.getTerm())
                        && (r.getAcademicYear() == null || b.getAcademicYear() == null
                        || b.getAcademicYear().equalsIgnoreCase(r.getAcademicYear())))
                .map(billingsUtil::mapBillings_ToBillingResponse)
                .collect(Collectors.toList()));
    }

    /**
     * Also used by FinancialBooksService for get-billing-by-institutionClass (Orb UI call)
     */
    public Optional<List<BillingsResponse>> fetchBillingByInstitutionClass(BillingFetchRequest r) {
        return Optional.of(BillingsUtil.billingGlobalList.stream()
                .filter(b -> b.getInstitutionCode().equalsIgnoreCase(r.getInstitutionCode())
                        && b.getStudentClass().equalsIgnoreCase(r.getStudentClass()))
                .map(billingsUtil::mapBillings_ToBillingResponse)
                .collect(Collectors.toList()));
    }
}