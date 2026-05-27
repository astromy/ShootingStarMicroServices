package com.astromyllc.shootingstar.onlineapplication.utils;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicationCategoryRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.ParentsRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.Students2Request;
import com.astromyllc.shootingstar.onlineapplication.dto.response.ApplicationsResponse;
import com.astromyllc.shootingstar.onlineapplication.dto.response.alien.ParentsResponse;
import com.astromyllc.shootingstar.onlineapplication.dto.response.alien.ProcessedApplicationResponse;
import com.astromyllc.shootingstar.onlineapplication.model.Applications;
import com.astromyllc.shootingstar.onlineapplication.model.Appointment;
import com.astromyllc.shootingstar.onlineapplication.model.Parents;
import com.astromyllc.shootingstar.onlineapplication.repository.ApplicationsRepository;
import com.astromyllc.shootingstar.onlineapplication.repository.AppointmentRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.groupingBy;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationUtilities {


    public static List<Applications> apl = null;
    public static List<Appointment> appointmentsGlobal = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static Long applicantIndex = 0L;
    private final ApplicationsRepository applicationsRepository;
    private final WebClient.Builder webClientBuilder;
    private final WebClient plainWebClient;
    private final AppointmentRepository appointmentRepository;
    private InstitutionRequest institutionRequest = null;
    @Value("${gateway.host}")
    private String host;


    @PostConstruct
    public void init() {
        log.info("Appointment List Fetched");
        appointmentsGlobal = appointmentRepository.findAll().stream()
                .filter(x -> x.getAppointmentDateTime().getYear() == LocalDate.now().getYear())
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)); // ✅ mutable

        log.info("Application List Fetched");
        apl = applicationsRepository.findAll().stream()
                .filter(x -> x.getApplicationDate().getYear() == LocalDate.now().getYear())
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)); // ✅ mutable

    }

    File filesystemRoot() throws URISyntaxException {
        return Paths.get((getClass().getClassLoader().getResource("static/applicationDocuments/Pictures/")).toURI()).toFile();
    }

    File docfilesystemRoot() throws URISyntaxException {
        return Paths.get((getClass().getClassLoader().getResource("static/applicationDocuments/BirthCerts/")).toURI()).toFile();
    }

    public ApplicationsResponse mapApplications_ToApplicationResponse(Applications applications1) {
        List<ParentsRequest> parentsRequests = Collections.emptyList();

        if (applications1.getParentsList() != null && !applications1.getParentsList().isEmpty()) {
            parentsRequests = new ArrayList<>(applications1.getParentsList().size());
            for (Parents parent : applications1.getParentsList()) {
                ParentsRequest request = new ParentsRequest();
                request.setId(parent.getId() != null ? parent.getId().toString() : null);
                request.setFirstNames(parent.getFirstNames());
                request.setLastName(parent.getLastName());
                request.setEmail(parent.getEmail());
                request.setContact1(parent.getContact1());
                request.setContact2(parent.getContact2());
                request.setOccupation(parent.getOccupation());
                request.setPlaceOfWork(parent.getPlaceOfWork());
                request.setParentType(parent.getParentType());
                request.setApplicantId(parent.getApplicantId());
                request.setInstitutionCode(parent.getInstitutionCode());
                parentsRequests.add(request);
            }
        }

        return ApplicationsResponse.builder()
                .idapplication(applications1.getIdapplication())
                .applicantFirstName(applications1.getApplicantFirstName())
                .applicantOtherName(applications1.getApplicantOtherName())
                .applicantLastName(applications1.getApplicantLastName())
                .applicantDateOfBirth(applications1.getApplicantDateOfBirth())
                .applicantPlaceOfBirth(applications1.getApplicantPlaceOfBirth())
                .applicantGender(applications1.getApplicantGender())
                .applicantCountryOfBirth(applications1.getApplicantCountryOfBirth())
                .applicantNationality(applications1.getApplicantNationality())
                .applicantPicture(applications1.getApplicantPicture())
                .applicantBirthCert(applications1.getApplicantBirthCert())
                .applicantDenomination(applications1.getApplicantDenomination())
                .applicationCode(applications1.getApplicationCode())
                .applicationStatus(applications1.getApplicationStatus())
                .applicationInstitution(applications1.getApplicationInstitution())
                .applicationType(applications1.getApplicationType())
                .applicationDate(applications1.getApplicationDate())
                .appointmentDate(applications1.getAppointmentDate())
                .nameOfPreviousSchool(applications1.getNameOfPreviousSchool())
                .classOfDeparture(applications1.getClassOfDeparture())
                .reasonForDeparture(applications1.getReasonForDeparture())
                .addressOfPreviousSchool(applications1.getAddressOfPreviousSchool())
                .contactOfPreviousSchool(applications1.getContactOfPreviousSchool())
                .studentParents(parentsRequests)
                .build();
    }

    public ApplicationsResponse mapApplications_ToApplicationResponseWithImage(Applications applications1) {
        List<ParentsRequest> parentsRequests = Collections.emptyList();

        if (applications1.getParentsList() != null && !applications1.getParentsList().isEmpty()) {
            parentsRequests = new ArrayList<>(applications1.getParentsList().size());
            for (Parents parent : applications1.getParentsList()) {
                // Direct constructor call - fastest approach
                parentsRequests.add(new ParentsRequest(
                        parent.getId() != null ? parent.getId().toString() : null,
                        parent.getFirstNames(),
                        parent.getLastName(),
                        parent.getEmail(),
                        parent.getContact1(),
                        parent.getContact2(),
                        parent.getOccupation(),
                        parent.getPlaceOfWork(),
                        parent.getParentType(),
                        parent.getApplicantId(),
                        parent.getInstitutionCode()
                ));
            }
        }

        return ApplicationsResponse.builder()
                .idapplication(applications1.getIdapplication())
                .applicantFirstName(applications1.getApplicantFirstName())
                .applicantOtherName(applications1.getApplicantOtherName())
                .applicantLastName(applications1.getApplicantLastName())
                .applicantDateOfBirth(applications1.getApplicantDateOfBirth())
                .applicantPlaceOfBirth(applications1.getApplicantPlaceOfBirth())
                .applicantGender(applications1.getApplicantGender())
                .applicantCountryOfBirth(applications1.getApplicantCountryOfBirth())
                .applicantNationality(applications1.getApplicantNationality())
                .applicantPicture(applications1.getApplicantPicture())
                .applicantBirthCert(applications1.getApplicantBirthCert())
                .applicantDenomination(applications1.getApplicantDenomination())
                .applicationCode(applications1.getApplicationCode())
                .applicationStatus(applications1.getApplicationStatus())
                .applicationInstitution(applications1.getApplicationInstitution())
                .applicationType(applications1.getApplicationType())
                .applicationDate(applications1.getApplicationDate())
                .appointmentDate(applications1.getAppointmentDate())
                .nameOfPreviousSchool(applications1.getNameOfPreviousSchool())
                .classOfDeparture(applications1.getClassOfDeparture())
                .reasonForDeparture(applications1.getReasonForDeparture())
                .addressOfPreviousSchool(applications1.getAddressOfPreviousSchool())
                .contactOfPreviousSchool(applications1.getContactOfPreviousSchool())
                .studentParents(parentsRequests)
                .build();
    }

    public Applications mapApplicationRequest_ToApplications(Students2Request applications1) throws IOException, URISyntaxException {
        String applicationCode = generateApplicationCode(applications1.getInstitutionCode());
        String appointmentDate = setAppointmentDate(applicationCode, applications1.getApplicationType());
        if (appointmentDate == null) return null;

        // Parallel file operations if they're independent
        String[] filePaths = saveFilesInParallel(applications1.getPicture(),
                applications1.getApplicantBirthCert(),
                applicationCode,
                applications1.getApplicantBirthCertFileType());

        // Direct conversion without stream overhead
        List<Parents> parentsList = convertParentsListDirect(applications1.getStudentParents(),
                applicationCode,
                applications1.getInstitutionCode());

        return Applications.builder()
                .idapplication(applications1.getIdapplication())
                .applicantOtherName(applications1.getOtherName())
                .applicantFirstName(applications1.getFirstName())
                .applicantLastName(applications1.getLastName())
                .applicantGender(applications1.getGender())
                .applicantDenomination(applications1.getDenomination())
                .applicantNationality(applications1.getNationality())
                .applicantPicture(filePaths[0])
                .applicantCountryOfBirth(applications1.getCountryOfBirth())
                .applicantPlaceOfBirth(applications1.getPlaceOfBirth())
                .applicantDateOfBirth(applications1.getDateOfBirth())
                .applicantBirthCert(filePaths[1])
                .applicationCode(applicationCode)
                .applicationStatus(applications1.getStatus())
                .applicationDate(LocalDate.now())
                .applicationInstitution(applications1.getInstitutionCode())
                .appointmentDate(LocalDateTime.parse(appointmentDate, formatter))
                .applicationType(applications1.getApplicationType())
                .nameOfPreviousSchool(applications1.getNameOfPreviousSchool())
                .addressOfPreviousSchool(applications1.getAddressOfPreviousSchool())
                .classOfDeparture(applications1.getClassOfDeparture())
                .contactOfPreviousSchool(applications1.getContactOfPreviousSchool())
                .reasonForDeparture(applications1.getReasonForDeparture())
                .parentsList(parentsList)
                .build();
    }

    private String[] saveFilesInParallel(String picture, String birthCert, String applicationCode, String fileType) {
        try {
            String fp = filesystemRoot().getPath();
            String fd = docfilesystemRoot().getAbsolutePath();

            // Use CompletableFuture for parallel execution
            CompletableFuture<String> pictureFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return saveFile(picture, applicationCode, fp);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save picture", e);
                }
            });

            CompletableFuture<String> birthCertFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return saveFile(birthCert, applicationCode, fd, fileType);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save birth certificate", e);
                }
            });

            return new String[]{pictureFuture.get(), birthCertFuture.get()};
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to access file system roots", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save files", e);
        }
    }

    private List<Parents> convertParentsListDirect(List<ParentsRequest> parentRequests, String applicationCode, String institutionCode) {
        if (parentRequests == null || parentRequests.isEmpty()) {
            return Collections.emptyList();
        }

        // Pre-allocate exact size to avoid resizing
        List<Parents> parentsList = new ArrayList<>(parentRequests.size());

        // Direct loop - faster than streams for small to medium lists
        for (ParentsRequest parentRequest : parentRequests) {
            Parents parent = new Parents();
            parent.setFirstNames(parentRequest.getFirstNames());
            parent.setLastName(parentRequest.getLastName());
            parent.setEmail(parentRequest.getEmail());
            parent.setContact1(parentRequest.getContact1());
            parent.setContact2(parentRequest.getContact2());
            parent.setOccupation(parentRequest.getOccupation());
            parent.setPlaceOfWork(parentRequest.getPlaceOfWork());
            parent.setParentType(parentRequest.getParentType());
            parent.setApplicantId(applicationCode);
            parent.setInstitutionCode(institutionCode);

            if (parentRequest.getId() != null) {
                parent.setId(new ObjectId(parentRequest.getId()));
            }

            parentsList.add(parent);
        }

        return parentsList;
    }

    private String generateApplicationCode(String applicationInstitution) {
        Map<String, String> body = Map.of("val", applicationInstitution);

        institutionRequest = plainWebClient.post()  // ✅ use this instead
                .uri(host + "/api/setup/getInstitutionByCode")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), Map.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(err -> Mono.error(
                                        new RuntimeException("Institution lookup failed: " + err)
                                ))
                )
                .bodyToMono(InstitutionRequest.class)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Failed to fetch institution [{}]: {}",
                        applicationInstitution, e.getMessage()))
                .block();

        if (institutionRequest == null) {
            throw new RuntimeException("Institution not found for code: " + applicationInstitution);
        }

        long applicantIndex = apl.stream()
                .filter(x -> x.getApplicationDate().getYear() == LocalDate.now().getYear())
                .count() + 1;

        return LocalDate.now().getYear() % 100 + "-" + applicationInstitution + "-" + applicantIndex;
    }

    private String setAppointmentDateX(String code, String type) {
        String commencement = institutionRequest.getAdmissions().getApplicationCategoryList().stream().filter(x -> x.getApplicationFormType().equalsIgnoreCase(type)).findFirst().get().getAppointmentCommencement();
        int appointmentsPerDay = institutionRequest.getAdmissions().getApplicationCategoryList().stream().filter(x -> x.getApplicationFormType().equalsIgnoreCase(type)).findFirst().get().getAppointmentPerDay();
        int formsQNT = institutionRequest.getAdmissions().getApplicationCategoryList().stream().filter(x -> x.getApplicationFormType().equalsIgnoreCase(type)).findFirst().get().getApplicationFormQNT();
        String closure = institutionRequest.getAdmissions().getApplicationCategoryList().stream().filter(x -> x.getApplicationFormType().equalsIgnoreCase(type)).findFirst().get().getAppointmentClosure();
        Map<LocalDateTime, List<Appointment>> map = appointmentsGlobal.stream()
                .filter(x -> x.getAppointmentDateTime().isAfter(LocalDateTime.parse(commencement.replace("T", " "), formatter))
                        && x.getAppointmentDateTime().isBefore(LocalDateTime.parse(closure.replace("T", " "), formatter)))
                .collect(groupingBy(Appointment::getAppointmentDateTime));
        Appointment ap = new Appointment();
        Object o = map.keySet().toArray()[0];

        if (!code.isEmpty()) {
            if (!map.isEmpty()) {
                int appointmentArraySize = map.get(o).size();
                if (map.get(o).get(0).getAppointmentDateTime().isBefore(LocalDateTime.parse(closure.replace("T", " "), formatter)) && formsQNT < appointmentArraySize) {
                    LocalDateTime apt = map.get(o).get(0).getAppointmentDateTime();
                    if (appointmentArraySize >= appointmentsPerDay) {
                        apt = apt.plusDays(1);
                    }
                    ap.setAppointmentDateTime(apt);
                    ap.setApplicationCode(code);
                    ap.setInstitutionId(code.split("-")[1]);
                    appointmentRepository.save(ap);
                    appointmentsGlobal.add(ap);
                    String result;
                    if (apt.toString().split(":").length < 3) {
                        result = apt.toString().replace("T", " ") + ":00";
                    } else {
                        result = apt.toString().replace("T", " ");
                    }
                    return result;
                }
            } else {
                ap.setAppointmentDateTime(LocalDateTime.parse(commencement.replace("T", " "), formatter));
                ap.setApplicationCode(code);
                ap.setInstitutionId(code.split("-")[1]);
                appointmentRepository.save(ap);
                appointmentsGlobal.add(ap);
                return commencement.replace("T", " ");
            }
        }
        return null;
    }

    private String setAppointmentDate(String code, String type) {

        // ✅ Extract once, reuse — avoids 4 identical stream operations
        ApplicationCategoryRequest category = institutionRequest.getAdmissions()
                .getApplicationCategoryList()
                .stream()
                .filter(x -> x.getApplicationFormType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No category found for type: " + type));

        String commencement = category.getAppointmentCommencement();
        String closure = category.getAppointmentClosure();
        int appointmentsPerDay = category.getAppointmentPerDay();
        int formsQNT = category.getApplicationFormQNT();

        LocalDateTime commencementDT = LocalDateTime.parse(commencement.replace("T", " "), formatter);
        LocalDateTime closureDT = LocalDateTime.parse(closure.replace("T", " "), formatter);

        if (code == null || code.isEmpty()) return null;

        Map<LocalDateTime, List<Appointment>> map = appointmentsGlobal.stream()
                .filter(x -> x.getAppointmentDateTime().isAfter(commencementDT)
                        && x.getAppointmentDateTime().isBefore(closureDT))
                .collect(groupingBy(Appointment::getAppointmentDateTime));

        Appointment ap = new Appointment();

        if (!map.isEmpty()) {
            LocalDateTime firstSlot = map.keySet().stream().sorted().findFirst().get();
            int appointmentArraySize = map.get(firstSlot).size();

            LocalDateTime apt;

            if (appointmentArraySize >= appointmentsPerDay) {
                // ✅ Slot is full — move to next available weekday
                apt = skipWeekends(firstSlot.plusDays(1));
            } else {
                // ✅ Slot has space — use it
                apt = skipWeekends(firstSlot);
            }

            if (apt.isBefore(closureDT)) {
                return getString(code, ap, apt);
            }

        } else {
            // No appointments yet — use commencement date
            LocalDateTime apt = skipWeekends(commencementDT);

            return getString(code, ap, apt);
        }

        return null;
    }

    @Nonnull
    private String getString(String code, Appointment ap, LocalDateTime apt) {
        ap.setAppointmentDateTime(apt);
        ap.setApplicationCode(code);
        ap.setInstitutionId(code.split("-")[1]);
        Appointment savedAp = appointmentRepository.save(ap);
        appointmentsGlobal.add(savedAp);

        String result = apt.toString().replace("T", " ");
        if (!result.matches(".*:\\d{2}:\\d{2}$")) {
            result = result + ":00";
        }
        return result;
    }

    private LocalDateTime skipWeekends(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY) {
            return dateTime.plusDays(2); // move to Monday
        } else if (day == DayOfWeek.SUNDAY) {
            return dateTime.plusDays(1); // move to Monday
        }
        return dateTime;
    }

    public String saveFile(String file, String fileName, String fileDirectoryPath, String fileType) throws IOException {
        if (fileType.equalsIgnoreCase("image/png") || fileType.equalsIgnoreCase("image/jpeg")) {
            fileName = fileName + ".png";
        } else {
            fileName = fileName + ".pdf";
        }
        String fileLocation = fileDirectoryPath + File.separator + fileName; // ✅ use File.separator not "\\"
        byte[] data = DatatypeConverter.parseBase64Binary(file); // ✅ fixed
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileLocation))) {
            outputStream.write(data);
        } // ✅ auto-close stream
        return fileLocation;
    }

    public String saveFile(String file, String fileName, String fileDirectoryPath) throws IOException {
        fileName = fileName + ".png";
        String fileLocation = fileDirectoryPath + File.separator + fileName; // ✅ use File.separator
        byte[] data = DatatypeConverter.parseBase64Binary(file); // ✅ fixed
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileLocation))) {
            outputStream.write(data);
        } // ✅ auto-close stream
        return fileLocation;
    }

    public ProcessedApplicationResponse mapApplications_ToProcessedApplicationResponse(Applications x) {
        return ProcessedApplicationResponse.builder()
                .applicantFirstName(x.getApplicantFirstName())
                .applicantLastName(x.getApplicantLastName())
                .applicantOtherName(x.getApplicantOtherName())
                .applicantGender(x.getApplicantGender())
                .applicantDateOfBirth(x.getApplicantDateOfBirth())
                .applicantPicture(x.getApplicantPicture())
                .applicantCountryOfBirth(x.getApplicantCountryOfBirth())
                .applicantPlaceOfBirth(x.getApplicantPlaceOfBirth())
                .applicantBirthCert(x.getApplicantBirthCert())
                .applicantBirthCertFileType("pdf")
                .applicantNationality(x.getApplicantNationality())
                .applicantDenomination(x.getApplicantDenomination())

                .nameOfPreviousSchool(x.getNameOfPreviousSchool())
                .contactOfPreviousSchool(x.getContactOfPreviousSchool())
                .addressOfPreviousSchool(x.getAddressOfPreviousSchool())
                .classOfDeparture(x.getClassOfDeparture())
                .reasonForDeparture(x.getReasonForDeparture())

                .applicationType(x.getApplicationType())
                .applicationInstitution(x.getApplicationInstitution())
                .applicationStatus("Processed")
                .parentsRequests(parentResponseBuilder(x))
                .build();
    }

    private List<ParentsResponse> parentResponseBuilder(Applications x) {
        List<ParentsResponse> pr = new ArrayList<>();

        if (x.getParentsList() != null && !x.getParentsList().isEmpty()) {
            for (Parents parent : x.getParentsList()) {
                pr.add(ParentsResponse.builder()
                        .email(parent.getEmail())
                        .institutionCode(parent.getInstitutionCode())
                        .parentType(parent.getParentType())
                        .contact1(parent.getContact1())
                        .contact2(parent.getContact2())
                        .placeOfWork(parent.getPlaceOfWork())
                        .occupation(parent.getOccupation())
                        .firstNames(parent.getFirstNames())
                        .lastName(parent.getLastName())
                        .build());
            }
        }

        return pr;
    }
}
