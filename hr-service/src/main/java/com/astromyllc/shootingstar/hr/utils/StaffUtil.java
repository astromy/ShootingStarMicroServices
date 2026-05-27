package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.DesignationListRequest;
import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffSubjectsRequest;
import com.astromyllc.shootingstar.hr.dto.request.alien.InstitutionRequest;
import com.astromyllc.shootingstar.hr.dto.response.*;
import com.astromyllc.shootingstar.hr.model.*;
import com.astromyllc.shootingstar.hr.repository.StaffRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffUtil {
    private final StaffRepository staffRepository;
    private final WebClient.Builder webClientBuilder;
    public static List<Staff> staffGlobalList;
    private static Long staffIndex = 0L;
    public static InstitutionRequest institutionRequest = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Value("${gateway.host}")
    private String host;

    private final ProfessionalRecordsUtil professionalRecordsUtil;
    private final StaffDocumentsUtil staffDocumentsUtil;
    private final DependantsUtil dependantsUtil;
    private final AcademicRecordsUtil academicRecordsUtil;
    private final StaffDesignationUtil staffDesignationUtil;
    private final StaffSubjectsUtil staffSubjectsUtil;

    @PostConstruct
    private void fetchAllStaff() {
        staffGlobalList = staffRepository.findAll();
        log.info("{} staff RECORDS FETCHED", staffGlobalList.size());
    }

    @Bean
    File filesystemRoot() throws URISyntaxException {
        return Paths.get((getClass().getClassLoader().getResource("static/staffDocuments/staffPictures/")).toURI()).toFile();
    }


    private String generateApplicationCode(String institutionCode) {
        SingleStringRequest request = SingleStringRequest.builder()
                .val(institutionCode)
                .build();
        institutionRequest =

                webClientBuilder
                        .baseUrl("http://"+host)
                        .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                            System.out.println("Request: " + clientRequest);
                            return Mono.just(clientRequest);
                        }))
                        .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                            System.out.println("Response: " + clientResponse);
                            return Mono.just(clientResponse);
                        }))
                        .build()
                        .post()
                        .uri("/api/setup/getInstitutionByCode")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(InstitutionRequest.class)
                        .block();
       /* staffIndex = staffGlobalList
                .stream()
                .filter(x -> x.getInstitutionCode().equalsIgnoreCase(institutionCode))
                .count() + 1;*/
        staffIndex = staffGlobalList.stream()
                .reduce((first, second) -> second)
                .map(staff -> {
                    String[] parts = staff.getStaffCode().split("-");
                    return parts.length > 1 ? Long.parseLong(parts[1]) : null;
                })
                .orElse(0L);
        staffIndex+=1;

        return "S" + institutionCode + "-" + staffIndex;

    }
    public  InstitutionRequest getInstitution(String institutionCode) {
        SingleStringRequest request = SingleStringRequest.builder()
                .val(institutionCode)
                .build();
      return  institutionRequest =

                webClientBuilder
                        .baseUrl("http://"+host)
                        .filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                            System.out.println("Request: " + clientRequest);
                            return Mono.just(clientRequest);
                        }))
                        .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                            System.out.println("Response: " + clientResponse);
                            return Mono.just(clientResponse);
                        }))
                        .build()
                        .post()
                        .uri("/api/setup/getInstitutionByCode")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(InstitutionRequest.class)
                        .block();

    }

    public Staff mapStaffRequest_ToStaff(StaffRequest staffRequest) throws URISyntaxException, IOException {
        String staffCode = generateApplicationCode(staffRequest.getInstitutionCode());
        String fp = filesystemRoot().getPath();
        String filepath = saveFile(staffRequest.getStaffPicture(), staffCode, fp);
        return Staff.builder()
                .dateOfBirth(LocalDate.parse(staffRequest.getDateOfBirth(), formatter))
                .dateOfEmployment(LocalDate.parse(staffRequest.getDateOfEmployment(), formatter))
                .staffCode(staffCode)
                .firstNames(staffRequest.getFirstNames())
                .lastName(staffRequest.getLastName())
                .nationality(staffRequest.getNationality())
                .homeTown(staffRequest.getHomeTown())
                .residentialTown(staffRequest.getResidentialTown())
                .contact1(staffRequest.getContact1())
                .backupContact(staffRequest.getBackupContact())
                .staffEmail(staffRequest.getStaffEmail())
                .nationalIDType(staffRequest.getNationalIDType())
                .nationalID(staffRequest.getNationalID())
                .snnitNumber(staffRequest.getSnnitNumber())
                .maritalStatus(staffRequest.getMaritalStatus())
                .nameOfSpouse(staffRequest.getNameOfSpouse())
                .gender(staffRequest.getGender())
                .level(staffRequest.getLevel())
                .designation(staffRequest.getDesignation())
                .staffPicture(filepath)
                .nextOfKing(staffRequest.getNextOfKing())
                .institutionCode(staffRequest.getInstitutionCode())
                //.professionalRecords(staffRequest.getProfessionalRecords().stream().map(p->ProfessionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(p)).toList())
                //.staffDocuments(staffRequest.getStaffDocuments().stream().map(sd-> StaffDocumentsUtil.mapStaffDocumentsRequest_ToStaffDocuents(sd)).toList())
                //.dependants(staffRequest.getDependants().stream().map(d->DependantsUtil.mapDependantsRequest_ToDependants(d)).toList())
                //.academicRecords(staffRequest.getAcademicRecords().stream().map(a->AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(a,newStaff.getStaffCode)).toList())
                // .portfolio(staffRequest.getPortfolio().stream().map(p->PortfolioUtil.mapPortFolioRequest_ToPortfolio(p,newStaff.getStaffCode)).toList())
                .build();
    }

    public Staff mapStaffRequest_ToStaff(StaffRequest staffRequest, Staff staff) throws URISyntaxException, IOException {
        staff.setDateOfBirth(LocalDate.parse(staffRequest.getDateOfBirth(), formatter));
        staff.setDateOfEmployment(LocalDate.parse(staffRequest.getDateOfEmployment(), formatter));
        staff.setStaffCode(Optional.ofNullable(staffRequest.getStaffCode()).orElse(staff.getStaffCode()));
        staff.setFirstNames(staffRequest.getFirstNames());
        staff.setLastName(staffRequest.getLastName());
        staff.setNationality(staffRequest.getNationality());
        staff.setHomeTown(staffRequest.getHomeTown());
        staff.setResidentialTown(staffRequest.getResidentialTown());
        staff.setContact1(staffRequest.getContact1());
        staff.setBackupContact(staffRequest.getBackupContact());
        staff.setStaffEmail(staffRequest.getStaffEmail());
        staff.setNationalIDType(staffRequest.getNationalIDType());
        staff.setNationalID(staffRequest.getNationalID());
        staff.setSnnitNumber(staffRequest.getSnnitNumber());
        staff.setMaritalStatus(staffRequest.getMaritalStatus());
        staff.setNameOfSpouse(staffRequest.getNameOfSpouse());
        staff.setGender(staffRequest.getGender());
        staff.setLevel(staffRequest.getLevel());
        staff.setDesignation(staffRequest.getDesignation());
        //staff.setStaffPicture(filepath);
        staff.setNextOfKing(staffRequest.getNextOfKing());
        return staff;
    }

    public StaffResponse mapStaff_ToStaffResponse(Staff staff) throws URISyntaxException, IOException {
        List<DependantsResponse> dr = Optional.ofNullable(staff.getDependants())
                .orElse(Collections.emptyList())
                .stream()
                .filter(d -> d.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode()))
                .map(DependantsUtil::mapDependants_ToDependantResponse)
                .toList();

        List<StaffDocumentsResponse> sdr = Optional.ofNullable(staff.getStaffDocuments())
                .orElse(Collections.emptyList())
                .stream()
                .filter(sd -> sd.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode()))
                .map(StaffDocumentsUtil::mapStaffDocuments_ToStaffDocuentsResponse)
                .toList();

        List<AcademicRecordsResponse> arr = Optional.ofNullable(staff.getAcademicRecords())
                .orElse(Collections.emptyList())
                .stream()
                .filter(ar -> ar.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode()))
                .map(AcademicRecordsUtil::mapAcademicRecord_ToAcademicRecordsResponse)
                .toList();

        List<ProfessionalRecordsResponse> prr = Optional.ofNullable(staff.getProfessionalRecords())
                .orElse(Collections.emptyList())
                .stream()
                .filter(pr -> pr.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode()))
                .map(ProfessionalRecordsUtil::mapProfessionalRecord_ToProfessionalRecordsResponse)
                .toList();

        List<DesignationListResponse> dl = Optional.ofNullable(staff.getStaffDesignations())
                .orElse(Collections.emptyList())
                .stream()
                .filter(ar -> ar.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode()))
                .map(StaffDesignationUtil::mapStaffDesignationList_ToStaffDesignationListResponse)
                .toList();

        List<StaffSubjectsResponse> ssr = Optional.ofNullable(staff.getStaffSubjects())
                .orElse(Collections.emptyList())
                .stream()
                .filter(ar -> ar.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode()))
                .map(StaffSubjectsUtil::mapStaffSubject_ToStaffSubjectsResponse)
                .toList();

        return StaffResponse.builder()
                .id(String.valueOf(staff.getId()))
                .dateOfBirth(staff.getDateOfBirth())
                .dateOfEmployment(staff.getDateOfEmployment())
                .staffCode(staff.getStaffCode())
                .firstNames(staff.getFirstNames())
                .lastName(staff.getLastName())
                .nationality(staff.getNationality())
                .homeTown(staff.getHomeTown())
                .residentialTown(staff.getResidentialTown())
                .contact1(staff.getContact1())
                .backupContact(staff.getBackupContact())
                .staffEmail(staff.getStaffEmail())
                .nationalIDType(staff.getNationalIDType())
                .nationalID(staff.getNationalID())
                .snnitNumber(staff.getSnnitNumber())
                .maritalStatus(staff.getMaritalStatus())
                .nameOfSpouse(staff.getNameOfSpouse())
                .gender(staff.getGender())
                .level(staff.getLevel())
                .designation(staff.getDesignation())
                .institutionCode(staff.getInstitutionCode())
                //.staffPicture(filepath)
                .nextOfKing(staff.getNextOfKing())
                .dependants(dr)
                .staffDocuments(sdr)
                .academicRecords(arr)
                .professionalRecords(prr)
                .staffDesignationResponseList(dl)
                .staffSubjectsResponseList(ssr)
                .build();
    }

    public String saveFile(String file, String fileName, String fileDirectoryPath) throws IOException {
        fileName = fileName + ".png";
        String fileLocation = fileDirectoryPath + "\\" + fileName;
        byte[] data = DatatypeConverter.parseBase64Binary(Arrays.toString(file.toCharArray()));
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileLocation));
        outputStream.write(data);
        return fileLocation;
    }

    public Optional<StaffResponse> createNewStaff(StaffRequest staffRequest) throws URISyntaxException, IOException {
        Staff newStaff = mapStaffRequest_ToStaff(staffRequest);

        processRecords(staffRequest.getProfessionalRecords(),
                (r -> ProfessionalRecordsUtil.mapProfessionalRecordRequest_ToProfessionalRecords(r, newStaff.getStaffCode())),
                professionalRecordsUtil::saveAll,
                newStaff::setProfessionalRecords);

        processRecords(staffRequest.getStaffDocuments(),
                r -> StaffDocumentsUtil.mapStaffDocumentsRequest_ToStaffDocuents(r, newStaff.getStaffCode()),
                staffDocumentsUtil::saveAll,
                newStaff::setStaffDocuments);

        processRecords(staffRequest.getDependants(),
                r -> DependantsUtil.mapDependantsRequest_ToDependants(r, newStaff.getStaffCode()),
                dependantsUtil::saveAll,
                newStaff::setDependants);

        processRecords(staffRequest.getAcademicRecords(),
                r -> AcademicRecordsUtil.mapAcademicRecordRequest_ToAcademicRecords(r, newStaff.getStaffCode()),
                academicRecordsUtil::saveAll,
                newStaff::setAcademicRecords);

        processRecords(staffRequest.getStaffDesignations(),
                StaffDesignationUtil::mapStaffDesignationListRequest_ToStaffDesignationList,
                staffDesignationUtil::saveAll,
                newStaff::setStaffDesignations);

        processRecords(staffRequest.getStaffSubjects(),
                StaffSubjectsUtil::mapStaffSubjectRequest_ToStaffSubjects,
                staffSubjectsUtil::saveAll,
                newStaff::setStaffSubjects);

        staffRepository.save(newStaff);
        StaffUtil.staffGlobalList.add(newStaff);

        return Optional.of(mapStaff_ToStaffResponse(newStaff));
    }


    public Optional<StaffResponse> updateExistingStaff(StaffRequest staffRequest, Staff existingStaff) throws URISyntaxException, IOException {
        Staff updatedStaff = mapStaffRequest_ToStaff(staffRequest, existingStaff);

        /*
        * Update Staff Professional Records
         */
        updateRecords(
                staffRequest.getProfessionalRecords(),
                existingStaff.getProfessionalRecords(),
                ProfessionalRecordsUtil::mapProfessionalRecordRequest_ToProfessionalRecords,
                professionalRecordsUtil::updateProfessionalRecord,
                professionalRecordsUtil::saveAll,
                updatedStaff::setProfessionalRecords,
                updatedStaff.getStaffCode(),
                (ent, req) -> ent.getNameOfInstitution().equalsIgnoreCase(req.getNameOfInstitution()) &&
                        ent.getDateOfEmployment().equals(req.getDateOfEmployment())
        );

        /*
         * Update Staff Dependant Records
         */
        updateRecords(
                staffRequest.getDependants(),
                existingStaff.getDependants(),
                DependantsUtil::mapDependantsRequest_ToDependants,
                DependantsUtil::updateDependants,
                dependantsUtil::saveAll,
                updatedStaff::setDependants,
                updatedStaff.getStaffCode(),
                (ent, req) -> ent.getName().equalsIgnoreCase(req.getName()) &&
                        ent.getDateOfBirth().equals(req.getDateOfBirth())
        );

        /*
         * Update Staff Academic Records
         */
        updateRecords(
                staffRequest.getAcademicRecords(),
                existingStaff.getAcademicRecords(),
                AcademicRecordsUtil::mapAcademicRecordRequest_ToAcademicRecords,
                academicRecordsUtil::updateAcademicRecord,
                academicRecordsUtil::saveAll,
                updatedStaff::setAcademicRecords,
                updatedStaff.getStaffCode(),
                (ent, req) -> ent.getNameOfInstitution().equalsIgnoreCase(req.getNameOfInstitution()) &&
                        ent.getDateOfGraduation().equals(req.getDateOfGraduation())
        );

        /*
         * Update Staff Documents Records
         */
        updateRecords(
                staffRequest.getStaffDocuments(),
                existingStaff.getStaffDocuments(),
                StaffDocumentsUtil::mapStaffDocumentsRequest_ToStaffDocuents,
                staffDocumentsUtil::updateStaffDocuments,
                staffDocumentsUtil::saveAll,
                updatedStaff::setStaffDocuments,
                updatedStaff.getStaffCode(),
                (ent, req) -> ent.getInstitutionCode().equalsIgnoreCase(req.getInstitutionCode()) &&
                        ent.getDocumentType().equals(req.getDocumentType())
        );

        /*
         * Update Staff Teaching Subjects Records
         */
        this.<StaffSubjectsRequest, StaffSubjects>updateRecords(
                staffRequest.getStaffSubjects(),
                existingStaff.getStaffSubjects(),
                (req, staffCode) -> StaffSubjectsUtil.mapStaffSubjectRequest_ToStaffSubjects(req),
                (existing, requestRecord, staffCode) -> StaffSubjectsUtil.updateStaffSubjects(existing, requestRecord),
                staffSubjectsUtil::saveAll,
                updatedStaff::setStaffSubjects,
                updatedStaff.getStaffCode(),
                (ent, req) -> ent.getSubject().equalsIgnoreCase(req.getSubject()) &&
                        ent.getSubjectClass().equalsIgnoreCase(req.getSubjectClass()) &&
                        ent.getInstitutionCode().equalsIgnoreCase(req.getInstitutionCode())
        );

        /*
         * Update Staff Designation Records
         */
        this.<DesignationListRequest, DesignationList>updateRecords(
                staffRequest.getStaffDesignations(),
                existingStaff.getStaffDesignations(),
                (req, staffCode) -> StaffDesignationUtil.mapStaffDesignationListRequest_ToStaffDesignationList(req),
                (existing, requestRecord, staffCode) -> staffDesignationUtil.updateStaffDesignation(existing, requestRecord),
                staffDesignationUtil::saveAll,
                updatedStaff::setStaffDesignations,
                updatedStaff.getStaffCode(),
                (ent, req) -> ent.getInstitutionCode().equalsIgnoreCase(req.getInstitutionCode()) &&
                        ent.getDesignation().equalsIgnoreCase(req.getDesignation())
        );


        staffRepository.save(updatedStaff);
        return Optional.of(mapStaff_ToStaffResponse(updatedStaff));
    }

    private <T, R> void processRecords(List<T> records,
                                       Function<T, R> mapper,
                                       Consumer<List<R>> saveFn,
                                       Consumer<List<R>> setter) {
        if (records != null && !records.isEmpty()) {
            List<R> mapped = records.stream().map(mapper).collect(Collectors.toList());
            saveFn.accept(mapped);
            setter.accept(mapped);
        }
    }
    private <REQ, ENT> void updateRecords(List<REQ> requestRecords,
                                          List<ENT> existingRecords,
                                          BiFunction<REQ, String, ENT> mapper,
                                          TriConsumer<ENT, REQ, String> updater,
                                          Consumer<List<ENT>> saveFn,
                                          Consumer<List<ENT>> setter,
                                          String staffCode,
                                          BiPredicate<ENT, REQ> matchPredicate) {
        if (requestRecords != null && !requestRecords.isEmpty()) {
            List<ENT> updated = requestRecords.stream()
                    .map(req -> {
                        Optional<ENT> existing =  Optional.ofNullable(existingRecords)
                                .orElse(Collections.emptyList())
                                .stream()
                                .filter(e -> matchPredicate.test(e, req))
                                .findFirst();
                        return existing.map(e -> {
                            updater.accept(e, req, staffCode);
                            return e;
                        }).orElseGet(() -> mapper.apply(req, staffCode));
                    })
                    .collect(Collectors.toList());
            saveFn.accept(updated);
            setter.accept(updated);
        }
    }

    @FunctionalInterface
    interface TriConsumer<T, U, V> {
        void accept(T t, U u, V v);
    }

    private boolean isSameRecord(Object existing, Object incoming) {
        if (existing instanceof ProfessionalRecords e && incoming instanceof ProfessionalRecords r) {
            return e.getNameOfInstitution().equalsIgnoreCase(r.getNameOfInstitution()) &&
                    e.getDateOfEmployment().equals(r.getDateOfEmployment());
        } else if (existing instanceof Dependants e && incoming instanceof Dependants r) {
            return e.getName().equalsIgnoreCase(r.getName()) &&
                    e.getDateOfBirth().equals(r.getDateOfBirth());
        } else if (existing instanceof AcademicRecords e && incoming instanceof AcademicRecords r) {
            return e.getNameOfInstitution().equalsIgnoreCase(r.getNameOfInstitution()) &&
                    e.getDateOfGraduation().equals(r.getDateOfGraduation());
        }
        return false;
    }

}
