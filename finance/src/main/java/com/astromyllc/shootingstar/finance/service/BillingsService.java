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
    public BillingsResponse updateBilling(BillingsRequest billingsRequest) {
        return null;
    }

    @Override
    public Optional<List<Student_BillResponse>> createBillings(BillingsRequest billingsRequest) {

     List<Billings> billings = billingsRequest.getStudentId().stream()
                .flatMap(stud -> BillUtil.billGlobalList.stream()
                        .filter(bill -> billingsRequest.getInstitutionCode().equalsIgnoreCase(bill.getInstitutionCode()) &&
                                billingsRequest.getBillname().stream()
                                        .anyMatch(request -> request.equalsIgnoreCase(bill.getBill_Name()))
                        )
                        .map(bill -> billingsUtil.mapBillingRequest_ToBilling(
                                billingsRequest.getInstitutionCode(),
                                billingsRequest.getStudentClass(),
                                billingsRequest.getTerm(),
                                bill.getBill_Amount(),
                                bill.getBill_Amount(),
                                bill.getBill_Description(),
                                bill.getBill_Name(),
                                stud)
                        )
                ).toList();
     billingsRepository.saveAll(billings);
     BillingsUtil.billingGlobalList.addAll(billings);

    return Optional.of( student_BillService.createStudentsBill(billings.stream()
            .collect(Collectors.groupingBy(Billings::getStudentId))  // Group by studentId
            .entrySet().stream()  // Stream over the grouped entries
            .map(entry -> {
                String studentId = entry.getKey();  // Get studentId from the group
                String studClass= entry.getValue().get(0).getStudentClass();
                String instCode= entry.getValue().get(0).getInstitutionCode();
                String term= entry.getValue().get(0).getTerm();
                Double totalAmount = entry.getValue().stream()  // Sum the billamnt for the group
                        .mapToDouble(Billings::getBillamnt)
                        .sum();

                // Now, map the summed data to a Student_BillRequest
                // We use the mapBilling_ToStudentBill method to convert
                // Create a new Billings object to pass into the map function
                Billings summarizedBilling = new Billings();
                summarizedBilling.setStudentId(studentId);
                summarizedBilling.setBillamnt(totalAmount);
                summarizedBilling.setTerm(term);
                summarizedBilling.setStudentClass(studClass);
                summarizedBilling.setInstitutionCode(instCode);

                // Use the existing map function to map to Student_BillRequest
                return billingsUtil.mapBilling_ToStudentBill(summarizedBilling);
            })
            .toList()));
    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingsByInstitution(BillingFetchRequest billFetchRequest) {
        return Optional.of(BillingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billFetchRequest.getInstitutionCode()))
                .map(billingsUtil::mapBillings_ToBillingResponse).toList());
    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingByInstitutionAndStudent(BillingFetchRequest billingFetchRequest) {
        return Optional.of(BillingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingFetchRequest.getInstitutionCode())
                        && b.getStudentId().equalsIgnoreCase(billingFetchRequest.getStudentId()))
                .map(billingsUtil::mapBillings_ToBillingResponse).toList());
    }

    @Override
    public Optional<List<BillingsResponse>> fetchBillingByInstitutionStudentClassTerm(BillingFetchRequest billingFetchRequest) {
        return Optional.of(BillingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingFetchRequest.getInstitutionCode())
                        && b.getStudentId().equalsIgnoreCase(billingFetchRequest.getStudentId())
                        && b.getStudentClass().equalsIgnoreCase(billingFetchRequest.getStudentClass())
                        && b.getTerm().equalsIgnoreCase(billingFetchRequest.getTerm()))
                .map(billingsUtil::mapBillings_ToBillingResponse).toList());
    }

    @Override
    public Optional<List<BillingsResponse>> fetchClassBillingByInstitution(BillingFetchRequest billingFetchRequest) {
        return Optional.of(BillingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingFetchRequest.getInstitutionCode())
                        && b.getStudentClass().equalsIgnoreCase(billingFetchRequest.getStudentClass())
                        && b.getTerm().equalsIgnoreCase(billingFetchRequest.getTerm()))
                .map(billingsUtil::mapBillings_ToBillingResponse).toList());
    }

    @Override
    public Optional<List<BillingsResponse>> fetchSchoolBillingByInstitution(BillingFetchRequest billingFetchRequest) {
        return Optional.of(BillingsUtil.billingGlobalList.stream().filter(
                b -> b.getInstitutionCode().equalsIgnoreCase(billingFetchRequest.getInstitutionCode())
                        && b.getTerm().equalsIgnoreCase(billingFetchRequest.getTerm()))
                .map(billingsUtil::mapBillings_ToBillingResponse).toList());
    }
}
