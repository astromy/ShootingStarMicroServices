package com.astromyllc.shootingstar.onlineapplication.utils;

import com.astromyllc.shootingstar.onlineapplication.dto.request.*;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.Collectors.groupingBy;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationUtilities {


    private static final AtomicLong applicantIndex = new AtomicLong(0L);
    public static List<Applications> apl = null;
    public static List<Appointment> appointmentsGlobal = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ApplicationsRepository applicationsRepository;
    private final MongoTemplate mongoTemplate;
    private final WebClient.Builder webClientBuilder;
    private final WebClient plainWebClient;
    private final AppointmentRepository appointmentRepository;
    private final MailUtil mailUtil;
    private InstitutionRequest institutionRequest = null;
    @Value("${gateway.host}")
    private String host;
    @Value("${paystack.secrete}")
    private String PAYSTACK_SECRET_KEY;


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

        long maxIndex = applicationsRepository.findAll().stream()
                .map(a -> a.getApplicationCode().split("-"))
                .filter(parts -> parts.length == 3)
                .mapToLong(parts -> {
                    try {
                        return Long.parseLong(parts[2]);
                    } catch (Exception e) {
                        return 0L;
                    }
                })
                .max().orElse(0L);
        applicantIndex.set(maxIndex);

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

        // ✅ Resolve institution name safely
        String institutionName = resolveInstitutionName(applications1.getApplicationInstitution());

        return ApplicationsResponse.builder()
                .idapplication(applications1.getIdapplication())
                .applicantFirstName(applications1.getApplicantFirstName())
                .applicantOtherName(applications1.getApplicantOtherName())
                .applicantLastName(applications1.getApplicantLastName())
                .admissionDate(applications1.getAdmissionDate())
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
                .applicationInstitutionName(institutionName)  // ✅ populated
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
        String institutionName = resolveInstitutionName(applications1.getApplicationInstitution());

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
                .applicationInstitutionName(institutionName)
                .build();
    }

    public Applications mapApplicationRequest_ToApplications(Students2Request applications1) throws IOException, URISyntaxException {
        String applicationCode = (applications1.getIdapplication() == null
                || applications1.getIdapplication().isBlank())
                ? generateApplicationCode(applications1.getInstitutionCode())
                : applications1.getIdapplication();
        String appointmentDate = setAppointmentDate(applicationCode, applications1.getApplicationType());
        if (appointmentDate == null) return null;

        // Parallel file operations if they're independent
        String[] filePaths = saveFilesInParallel(applications1.getPicture(),
                applications1.getBirthCert(),
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
                .applicationInstitutionName(
                        institutionRequest != null ? institutionRequest.getName() : applications1.getInstitutionCode()
                )
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

    public Applications UpdateApplication(Applications application, ApplicantStudentSkimRequest request) throws IOException, URISyntaxException {
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            application.setApplicationStatus(request.getStatus());
        }
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            application.setApplicantDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        }
        if (request.getGender() != null && !request.getGender().isBlank()) {
            application.setApplicantGender(request.getGender());
        }
        if (request.getNationality() != null && !request.getNationality().isBlank()) {
            application.setApplicantNationality(request.getNationality());
        }
        if (request.getDenomination() != null && !request.getDenomination().isBlank()) {
            application.setApplicantDenomination(request.getDenomination());
        }
        if (request.getInstitutionCode() != null && !request.getInstitutionCode().isBlank()) {
            application.setApplicationInstitution(request.getInstitutionCode());
        }
        application.setAdmissionDate(LocalDate.now());

        return application;
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

            if (parentRequest.getId() != null && !parentRequest.getId().isBlank()) {
                parent.setId(new ObjectId(parentRequest.getId()));
            }

            Parents savedParent = mongoTemplate.save(parent);
            parentsList.add(savedParent);
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

        /*applicantIndex = apl.stream()
                .filter(x -> x.getApplicationDate().getYear() == LocalDate.now().getYear())
                .count() + 1;*/

        return LocalDate.now().getYear() % 100 + "-" + applicationInstitution + "-" + applicantIndex.incrementAndGet();
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

    private String setAppointmentDateXX(String code, String type) {

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
                return getString(code, ap, apt, type);
            }

        } else {
            // No appointments yet — use commencement date
            LocalDateTime apt = skipWeekends(commencementDT);

            return getString(code, ap, apt, type);
        }

        return null;
    }

    private synchronized String setAppointmentDate(String code, String type) {

        ApplicationCategoryRequest category = institutionRequest.getAdmissions()
                .getApplicationCategoryList()
                .stream()
                .filter(x -> x.getApplicationFormType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No category found for type: " + type));

        String commencement = category.getAppointmentCommencement();
        String closure = category.getAppointmentClosure();
        int appointmentsPerDay = category.getAppointmentPerDay();

        LocalDateTime commencementDT = LocalDateTime.parse(commencement.replace("T", " "), formatter);
        LocalDateTime closureDT = LocalDateTime.parse(closure.replace("T", " "), formatter);

        if (code == null || code.isEmpty()) return null;

        // ✅ Guard against zero or negative
        if (appointmentsPerDay <= 0) {
            log.warn("appointmentsPerDay is {} for type {} — defaulting to 1", appointmentsPerDay, type);
            appointmentsPerDay = 1;
        }

        // ✅ Window: institution commencement time → 12:00 noon (hard cap)
        int startMinutes = commencementDT.getHour() * 60 + commencementDT.getMinute();
        int noonMinutes = 12 * 60;
        int availableMinutes = noonMinutes - startMinutes;

        // ✅ If institution sets commencement at or after noon — reject and log
        if (availableMinutes <= 0) {
            log.error("Commencement time {} is at or after noon. Cannot schedule appointments.", commencementDT);
            return null;
        }

        // ✅ Auto-calculate interval — spread evenly across window
        // Floor at 15 mins (too many appointments)
        // Ceil at availableMinutes (too few — single appointment gets full window start time)
        int intervalMinutes = availableMinutes / appointmentsPerDay;
        intervalMinutes = Math.max(15, intervalMinutes);

        log.info("Scheduling — commencement: {}, noon cap: 12:00, interval: {} mins, perDay: {}",
                commencementDT.toLocalTime(), intervalMinutes, appointmentsPerDay);

        // ✅ Group by date only
        // ✅ Extract institution id once for use in lambda
        final String institutionId = code.split("-")[1];
        final int finalAppointmentsPerDay = appointmentsPerDay;
        final int finalIntervalMinutes = intervalMinutes;

// ✅ Group by date — filtered by institution AND type
        Map<LocalDate, List<Appointment>> map = appointmentsGlobal.stream()
                .filter(x -> {
                    LocalDate apptDate = x.getAppointmentDateTime().toLocalDate();
                    LocalDate startDate = commencementDT.toLocalDate();
                    LocalDate endDate = closureDT.toLocalDate();
                    boolean inWindow = !apptDate.isBefore(startDate) && apptDate.isBefore(endDate);
                    boolean sameInstitution = institutionId.equals(x.getInstitutionId());
                    boolean sameType = type.equalsIgnoreCase(x.getApplicationType());
                    return inWindow && sameInstitution && sameType;
                })
                .collect(groupingBy(x -> x.getAppointmentDateTime().toLocalDate()));

        Appointment ap = new Appointment();

        if (!map.isEmpty()) {
            LocalDate availableDate = map.keySet().stream()
                    .sorted()
                    .filter(date -> map.get(date).size() < finalAppointmentsPerDay)
                    .findFirst()
                    .orElse(null);

            LocalDateTime apt;

            if (availableDate == null) {
                LocalDate lastDate = map.keySet().stream()
                        .sorted()
                        .reduce((first, second) -> second)
                        .get();
                apt = skipWeekends(lastDate.plusDays(1)
                        .atTime(commencementDT.getHour(), commencementDT.getMinute()));
            } else {
                int bookingsOnThisDay = map.get(availableDate).size();
                LocalDateTime proposedTime = availableDate
                        .atTime(commencementDT.getHour(), commencementDT.getMinute())
                        .plusMinutes((long) bookingsOnThisDay * finalIntervalMinutes);

                if (proposedTime.getHour() >= 12) {
                    log.warn("Proposed time {} exceeds noon — moving {} to next day", proposedTime, code);
                    apt = skipWeekends(availableDate.plusDays(1)
                            .atTime(commencementDT.getHour(), commencementDT.getMinute()));
                } else {
                    apt = proposedTime;
                }
            }

            if (apt.isBefore(closureDT)) {
                return getString(code, ap, apt, type);
            }

        } else {
            LocalDateTime apt = skipWeekends(commencementDT);
            return getString(code, ap, apt, type);
        }

        return null;
    }

    // ✅ Add synchronized to prevent race condition
    private synchronized String setAppointmentDateXXX(String code, String type) {

        ApplicationCategoryRequest category = institutionRequest.getAdmissions()
                .getApplicationCategoryList()
                .stream()
                .filter(x -> x.getApplicationFormType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No category found for type: " + type));

        String commencement = category.getAppointmentCommencement();
        String closure = category.getAppointmentClosure();
        int appointmentsPerDay = category.getAppointmentPerDay();

        LocalDateTime commencementDT = LocalDateTime.parse(commencement.replace("T", " "), formatter);
        LocalDateTime closureDT = LocalDateTime.parse(closure.replace("T", " "), formatter);

        if (code == null || code.isEmpty()) return null;

        log.info("Scheduling appointment — appointmentsPerDay: {}, commencement: {}, closure: {}, globalList size: {}",
                appointmentsPerDay, commencementDT, closureDT, appointmentsGlobal.size());

        // ✅ Fix: use !isBefore instead of isAfter to include the commencement datetime itself
        Map<LocalDateTime, List<Appointment>> map = appointmentsGlobal.stream()
                .filter(x -> !x.getAppointmentDateTime().isBefore(commencementDT)
                        && x.getAppointmentDateTime().isBefore(closureDT))
                .collect(groupingBy(Appointment::getAppointmentDateTime));

        log.info("Existing appointments in window: {}", map.size());

        Appointment ap = new Appointment();

        if (!map.isEmpty()) {
            // ✅ Find first slot that still has space
            LocalDateTime availableSlot = map.keySet().stream()
                    .sorted()
                    .filter(slot -> map.get(slot).size() < appointmentsPerDay)
                    .findFirst()
                    .orElse(null);

            LocalDateTime apt;

            if (availableSlot == null) {
                // All slots full — move to day after last booked slot
                LocalDateTime lastSlot = map.keySet().stream()
                        .sorted()
                        .reduce((first, second) -> second)
                        .get();
                apt = skipWeekends(lastSlot.plusDays(1));
                log.info("All slots full, moving to next day: {}", apt);
            } else {
                apt = availableSlot; // ✅ no need to skipWeekends — slot already exists
                log.info("Using existing slot with space: {}", apt);
            }

            if (apt.isBefore(closureDT)) {
                return getString(code, ap, apt, type);
            }

        } else {
            // No appointments yet — use commencement date
            LocalDateTime apt = skipWeekends(commencementDT);
            log.info("No existing appointments, using commencement: {}", apt);
            return getString(code, ap, apt, type);
        }

        return null;
    }

    @Nonnull
    // ✅ Updated signature
    private String getString(String code, Appointment ap, LocalDateTime apt, String type) {
        ap.setAppointmentDateTime(apt);
        ap.setApplicationCode(code);
        ap.setInstitutionId(code.split("-")[1]);
        ap.setApplicationType(type);
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
            return dateTime.plusDays(2);
        } else if (day == DayOfWeek.SUNDAY) {
            return dateTime.plusDays(1);
        }
        return dateTime;
    }

    private LocalDateTime normalizeToWorkingHours(LocalDateTime dateTime) {
        int hour = dateTime.getHour();

        // Before 8am → set to 8am
        if (hour < 8) {
            return dateTime.withHour(8).withMinute(0).withSecond(0);
        }

        // After 3pm → move to next working day at 8am
        if (hour >= 15) {
            LocalDateTime nextDay = dateTime.plusDays(1).withHour(8).withMinute(0).withSecond(0);
            // Check if next day is a weekend
            DayOfWeek nextDayOfWeek = nextDay.getDayOfWeek();
            if (nextDayOfWeek == DayOfWeek.SATURDAY) {
                nextDay = nextDay.plusDays(2);
            } else if (nextDayOfWeek == DayOfWeek.SUNDAY) {
                nextDay = nextDay.plusDays(1);
            }
            return nextDay;
        }

        // Between 8am and 3pm → fine as-is
        return dateTime;
    }

    public String saveFile(String file, String fileName, String fileDirectoryPath, String fileType) throws IOException {

        // TEMPORARY DIAGNOSTIC — remove after fix confirmed
        log.info("saveFile called — fileName: {}, fileType: {}, inputLength: {}",
                fileName, fileType, file == null ? "NULL" : file.length());
        log.info("First 100 chars of input: {}",
                file == null ? "NULL" : file.substring(0, Math.min(100, file.length())));

        if (file == null || file.isBlank()) {
            throw new IOException("File input is null or blank for: " + fileName);
        }

        if (fileType.equalsIgnoreCase("image/png") || fileType.equalsIgnoreCase("image/jpeg")) {
            fileName = fileName + ".png";
        } else {
            fileName = fileName + ".pdf";
        }

        // ✅ Strip data URL prefix if present: "data:application/pdf;base64,XXXX"
        String base64Data = file;
        if (base64Data != null && base64Data.contains(",")) {
            base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
        }

        // ✅ Remove any whitespace/newlines that corrupt base64 decoding
        base64Data = base64Data.replaceAll("\\s", "");

        if (base64Data.isEmpty()) {
            throw new IOException("Base64 data is empty for file: " + fileName);
        }

        byte[] data = DatatypeConverter.parseBase64Binary(base64Data);

        if (data.length == 0) {
            throw new IOException("Decoded file is empty for: " + fileName);
        }

        String fileLocation = fileDirectoryPath + File.separator + fileName;
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileLocation))) {
            outputStream.write(data);
        }

        log.info("Saved file: {} ({} bytes)", fileLocation, data.length);
        return fileName;
    }

    public String saveFile(String file, String fileName, String fileDirectoryPath) throws IOException {
        if (file == null || file.isBlank()) {
            throw new IOException("File input is null or blank for: " + fileName);
        }

        fileName = fileName + ".png";

        String base64Data = file;
        if (base64Data != null && base64Data.contains(",")) {
            base64Data = base64Data.substring(base64Data.indexOf(",") + 1);
        }
        base64Data = base64Data.replaceAll("\\s", "");

        byte[] data = DatatypeConverter.parseBase64Binary(base64Data);

        String fileLocation = fileDirectoryPath + File.separator + fileName;
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileLocation))) {
            outputStream.write(data);
        }

        log.info("Saved picture: {} ({} bytes)", fileLocation, data.length);
        return fileName;
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

    public Mono<ResponseEntity<String>> refundPayment(RefundRequest refundRequest) {
        log.info("Initiating refund for reference: {}", refundRequest.getReference());

        Map<String, Object> body = new HashMap<>();
        body.put("transaction", refundRequest.getReference());
        body.put("amount", refundRequest.getAmount()); // optional — omit to refund full amount

        return plainWebClient.post()
                .uri("https://api.paystack.co/refund")
                .header("Authorization", "Bearer " + PAYSTACK_SECRET_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(err -> Mono.error(
                                        new RuntimeException("Paystack refund failed: " + err)
                                ))
                )
                .bodyToMono(String.class)
                .map(res -> {
                    log.info("Refund successful for reference: {}", refundRequest.getReference());
                    return ResponseEntity.ok(res);
                })
                .doOnError(e -> log.error("Refund error for reference {}: {}",
                        refundRequest.getReference(), e.getMessage()));
    }

    private String resolveInstitutionName(String institutionCode) {
        // ✅ Case 1 — already fetched during this request cycle
        if (institutionRequest != null
                && institutionCode != null
                && institutionCode.equals(institutionRequest.getBececode())) {
            return institutionRequest.getName();
        }

        // ✅ Case 2 — need to fetch (e.g. called from a list/GET endpoint)
        if (institutionCode != null && !institutionCode.isBlank()) {
            try {
                Map<String, String> body = Map.of("val", institutionCode);
                InstitutionRequest fetched = plainWebClient.post()
                        .uri(host + "/api/setup/getInstitutionByCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.just(body), Map.class)
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                response -> response.bodyToMono(String.class)
                                        .flatMap(err -> Mono.error(new RuntimeException(err)))
                        )
                        .bodyToMono(InstitutionRequest.class)
                        .subscribeOn(Schedulers.boundedElastic())
                        .block();

                if (fetched != null) return fetched.getName();
            } catch (Exception e) {
                log.warn("Could not resolve institution name for code [{}]: {}", institutionCode, e.getMessage());
            }
        }

        // ✅ Case 3 — fallback
        return institutionCode;
    }


    private String saveMultipartFile(MultipartFile file, String applicationCode, String type) throws IOException, URISyntaxException {
        String extension = "";
        String directory;

        if (type.equals("picture")) {
            extension = ".png";
            directory = filesystemRoot().getPath();
        } else {
            extension = ".pdf";
            directory = docfilesystemRoot().getPath();
        }

        String fileName = applicationCode + extension;
        String filePath = directory + File.separator + fileName;

        file.transferTo(new File(filePath));
        log.info("Saved {} to: {} ({} bytes)", type, filePath, file.getSize());

        return fileName;
    }


    public boolean sendmail(String recipient, String subject,
                            String mailBody, String fromName, boolean isImportant) {
        try {
            return mailUtil.sendTransactionalEmail(
                    recipient,
                    subject,
                    mailBody,
                    fromName,
                    isImportant
            );
        } catch (Exception e) {
            log.error("Failed to send email to {}", recipient, e);
            return false;
        }
    }

    public String buildApplicationConfirmationEmail(Applications app) {
        String parentName = "";

        if (app.getParentsList() != null && !app.getParentsList().isEmpty()) {
            Parents parent = app.getParentsList().get(0);
            parentName = parent.getFirstNames() + " " + parent.getLastName();
        }

        String appointmentFormatted = app.getAppointmentDate() != null
                ? app.getAppointmentDate().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'at' HH:mm"))
                : "To be confirmed";

        String applicationDateFormatted = app.getApplicationDate() != null
                ? app.getApplicationDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                : LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        String institutionName = app.getApplicationInstitutionName() != null
                ? app.getApplicationInstitutionName()
                : app.getApplicationInstitution();

        String applicantFullName = ((app.getApplicantFirstName() != null ? app.getApplicantFirstName() : "") + " "
                + (app.getApplicantOtherName() != null ? app.getApplicantOtherName() : "") + " "
                + (app.getApplicantLastName() != null ? app.getApplicantLastName() : "")).trim();

        String dobFormatted = app.getApplicantDateOfBirth() != null
                ? app.getApplicantDateOfBirth().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                : "N/A";

        String year = String.valueOf(LocalDate.now().getYear());

        return getEmailTemplate()
                .replace("{{INSTITUTION_NAME}}", institutionName)
                .replace("{{APPLICATION_CODE}}", app.getApplicationCode())
                .replace("{{APPLICATION_TYPE}}", app.getApplicationType() != null ? app.getApplicationType() : "N/A")
                .replace("{{PARENT_NAME}}", parentName)
                .replace("{{APPLICANT_NAME}}", applicantFullName)
                .replace("{{DATE_OF_BIRTH}}", dobFormatted)
                .replace("{{APPLICATION_DATE}}", applicationDateFormatted)
                .replace("{{APPLICATION_STATUS}}", app.getApplicationStatus() != null ? app.getApplicationStatus() : "APPLIED")
                .replace("{{APPOINTMENT_DATE}}", appointmentFormatted)
                .replace("{{YEAR}}", year);
    }

    public String buildAdmissionStatusEmail(Applications app) {
        String parentName = "";

        if (app.getParentsList() != null && !app.getParentsList().isEmpty()) {
            Parents parent = app.getParentsList().get(0);
            parentName = parent.getFirstNames() + " " + parent.getLastName();
        }

        String applicationDateFormatted = app.getApplicationDate() != null
                ? app.getApplicationDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                : LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        String institutionName = app.getApplicationInstitutionName() != null
                ? app.getApplicationInstitutionName()
                : app.getApplicationInstitution();

        String applicantFullName = ((app.getApplicantFirstName() != null ? app.getApplicantFirstName() : "") + " "
                + (app.getApplicantOtherName() != null ? app.getApplicantOtherName() : "") + " "
                + (app.getApplicantLastName() != null ? app.getApplicantLastName() : "")).trim();

        String status = app.getApplicationStatus() != null ? app.getApplicationStatus() : "APPLIED";
        String year = String.valueOf(LocalDate.now().getYear());

        // Derive values that change based on approved vs rejected
        boolean approved = status.equalsIgnoreCase("APPROVED");
        String statusColor = approved ? "#27ae60" : "#e74c3c";
        String statusBarText = approved ? "✓ Application Approved" : "✗ Application Unsuccessful";
        String statusBarBg = approved ? "#27ae60" : "#e74c3c";
        String subjectiveNoun = approved ? "pleased" : "sorry";

        String outcomeMessage = approved
                ? "We are <strong>pleased to inform you</strong> that your child's application to "
                  + "<strong>" + institutionName + "</strong> has been <strong style=\"color:#27ae60;\">approved</strong>. "
                  + "We look forward to welcoming your child and will be in touch shortly with next steps."
                : "We regret to inform you that your child's application to "
                  + "<strong>" + institutionName + "</strong> has been <strong style=\"color:#e74c3c;\">unsuccessful</strong> "
                  + "at this time. We appreciate the interest shown and encourage you to reapply in future intakes.";

        String portalUrl = "https://prospectus.astromyllc.com";

        String nextSteps = approved
                ? """
                <table width="100%" cellpadding="0" cellspacing="0" border="0"
                       style="background:#eafaf1;border:1px solid #a9dfbf;border-radius:8px;margin:0 0 25px;">
                    <tr>
                        <td style="padding:16px 20px;">
                            <p style="margin:0 0 10px;font-size:13px;font-weight:700;color:#1e8449;">
                                Next Steps — Complete Your Admission
                            </p>
                            <ul style="margin:0;padding-left:18px;color:#1a5c31;font-size:13px;line-height:1.9;">
                                <li>Visit the admission portal to complete the process:
                                    <a href="{{PORTAL_URL}}" style="color:#2980b9;font-weight:700;">
                                        {{PORTAL_URL}}
                                    </a>
                                </li>
                                <li>You will need your child's application code: <strong>{{APPLICATION_CODE}}</strong></li>
                                <li>Bring all <strong>original documents</strong> on your first day.</li>
                                <li>Complete any outstanding registration requirements promptly.</li>
                            </ul>
                        </td>
                    </tr>
                </table>
                
                <!-- Portal CTA Button -->
                <table width="100%" cellpadding="0" cellspacing="0" border="0" style="margin:0 0 25px;">
                    <tr>
                        <td align="center">
                            <a href="{{PORTAL_URL}}"
                               style="display:inline-block;padding:14px 32px;background:#27ae60;
                                      color:#ffffff;text-decoration:none;border-radius:6px;
                                      font-size:14px;font-weight:700;letter-spacing:0.5px;">
                                Complete Admission →
                            </a>
                            <p style="margin:10px 0 0;font-size:12px;color:#7f8c8d;">
                                Use application code <strong>{{APPLICATION_CODE}}</strong> to log in
                            </p>
                        </td>
                    </tr>
                </table>
                """
                : """
                <table width="100%" cellpadding="0" cellspacing="0" border="0"
                       style="background:#fef9e7;border:1px solid #f9e79f;border-radius:8px;margin:0 0 25px;">
                    <tr>
                        <td style="padding:16px 20px;">
                            <p style="margin:0 0 10px;font-size:13px;font-weight:700;color:#d68910;">
                                What Happens Next
                            </p>
                            <ul style="margin:0;padding-left:18px;color:#7d6608;font-size:13px;line-height:1.9;">
                                <li>You may contact the admissions office for feedback on this decision.</li>
                                <li>You are welcome to reapply during the next admissions period.</li>
                                <li>Quote <strong>{{APPLICATION_CODE}}</strong> for any enquiries.</li>
                            </ul>
                        </td>
                    </tr>
                </table>
                """;
        nextSteps = nextSteps.replace("{{APPLICATION_CODE}}", app.getApplicationCode());
        return getAdmissionStatusTemplate()
                .replace("{{INSTITUTION_NAME}}", institutionName)
                .replace("{{APPLICATION_CODE}}", app.getApplicationCode())
                .replace("{{APPLICATION_TYPE}}", app.getApplicationType() != null ? app.getApplicationType() : "N/A")
                .replace("{{PARENT_NAME}}", parentName)
                .replace("{{APPLICANT_NAME}}", applicantFullName)
                .replace("{{APPLICATION_DATE}}", applicationDateFormatted)
                .replace("{{APPLICATION_STATUS}}", status)
                .replace("{{STATUS_COLOR}}", statusColor)
                .replace("{{STATUS_BAR_TEXT}}", statusBarText)
                .replace("{{STATUS_BAR_BG}}", statusBarBg)
                .replace("{{OUTCOME_MESSAGE}}", outcomeMessage)
                .replace("{{NEXT_STEPS}}", nextSteps)
                .replace("{{PORTAL_URL}}", portalUrl)   // ← new
                .replace("{{YEAR}}", year);
    }

    private String getEmailTemplate() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="x-apple-disable-message-reformatting">
                    <title>Application Confirmation</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f9;font-family:'Segoe UI',Arial,sans-serif;">
                
                <div style="display:none;max-height:0;overflow:hidden;mso-hide:all;">
                    {{INSTITUTION_NAME}} — Application received. Code: {{APPLICATION_CODE}}
                </div>
                
                <table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f6f9;">
                    <tr>
                        <td align="center" style="padding:30px 10px;">
                            <table width="600" cellpadding="0" cellspacing="0" border="0"
                                   style="background:#ffffff;border-radius:10px;overflow:hidden;
                                          box-shadow:0 2px 10px rgba(0,0,0,0.08);max-width:600px;width:100%;">
                
                                <!-- Header -->
                                <tr>
                                    <td style="background:linear-gradient(135deg,#1a3c5e,#2980b9);
                                               padding:30px 40px;text-align:center;">
                                        <h1 style="color:#ffffff;margin:0;font-size:22px;
                                                   font-weight:700;letter-spacing:1px;">
                                            {{INSTITUTION_NAME}}
                                        </h1>
                                        <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;font-size:13px;">
                                            {{APPLICATION_TYPE}} Admissions
                                        </p>
                                    </td>
                                </tr>
                
                                <!-- Status Bar -->
                                <tr>
                                    <td style="background:#27ae60;padding:10px;text-align:center;">
                                        <p style="color:#ffffff;margin:0;font-size:13px;
                                                  font-weight:600;letter-spacing:1px;text-transform:uppercase;">
                                            ✓ Application Successfully Received
                                        </p>
                                    </td>
                                </tr>
                
                                <!-- Body -->
                                <tr>
                                    <td style="padding:35px 40px;">
                
                                        <p style="color:#2c3e50;font-size:15px;margin:0 0 20px;">
                                            Dear <strong>{{PARENT_NAME}}</strong>,
                                        </p>
                                        <p style="color:#555;font-size:14px;line-height:1.7;margin:0 0 25px;">
                                            We are pleased to confirm receipt of your child's application at
                                            <strong>{{INSTITUTION_NAME}}</strong>.
                                            Please keep the details below for your records.
                                        </p>
                
                                        <!-- Details Box -->
                                        <table width="100%" cellpadding="0" cellspacing="0" border="0"
                                               style="background:#f8fafc;border-radius:8px;
                                                      border-left:4px solid #2980b9;margin:0 0 25px;">
                                            <tr>
                                                <td style="padding:20px 25px;">
                                                    <p style="margin:0 0 5px;font-size:11px;color:#7f8c8d;
                                                              text-transform:uppercase;letter-spacing:1px;">
                                                        Application Code
                                                    </p>
                                                    <p style="margin:0 0 15px;font-size:20px;font-weight:700;color:#1a3c5e;">
                                                        {{APPLICATION_CODE}}
                                                    </p>
                                                    <table width="100%" cellpadding="6" cellspacing="0">
                                                        <tr>
                                                            <td style="font-size:12px;color:#7f8c8d;width:45%;">Applicant Name</td>
                                                            <td style="font-size:13px;color:#2c3e50;font-weight:600;">{{APPLICANT_NAME}}</td>
                                                        </tr>
                                                        <tr style="background:#f0f4f8;">
                                                            <td style="font-size:12px;color:#7f8c8d;">Date of Birth</td>
                                                            <td style="font-size:13px;color:#2c3e50;font-weight:600;">{{DATE_OF_BIRTH}}</td>
                                                        </tr>
                                                        <tr>
                                                            <td style="font-size:12px;color:#7f8c8d;">Application Type</td>
                                                            <td style="font-size:13px;color:#2c3e50;font-weight:600;">{{APPLICATION_TYPE}}</td>
                                                        </tr>
                                                        <tr style="background:#f0f4f8;">
                                                            <td style="font-size:12px;color:#7f8c8d;">Application Date</td>
                                                            <td style="font-size:13px;color:#2c3e50;font-weight:600;">{{APPLICATION_DATE}}</td>
                                                        </tr>
                                                        <tr>
                                                            <td style="font-size:12px;color:#7f8c8d;">Status</td>
                                                            <td style="font-size:13px;color:#27ae60;font-weight:700;">{{APPLICATION_STATUS}}</td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                
                                        <!-- Appointment Box -->
                                        <table width="100%" cellpadding="0" cellspacing="0" border="0"
                                               style="background:linear-gradient(135deg,#eaf2fb,#d6eaf8);
                                                      border-radius:8px;margin:0 0 25px;">
                                            <tr>
                                                <td style="padding:18px 22px;">
                                                    <p style="margin:0 0 5px;font-size:11px;color:#7f8c8d;
                                                              text-transform:uppercase;letter-spacing:1px;">
                                                        Your Appointment Date &amp; Time
                                                    </p>
                                                    <p style="margin:0;font-size:16px;font-weight:700;color:#1a3c5e;">
                                                        {{APPOINTMENT_DATE}}
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                
                                        <!-- Notice -->
                                        <table width="100%" cellpadding="0" cellspacing="0" border="0"
                                               style="background:#fef9e7;border:1px solid #f9e79f;
                                                      border-radius:8px;margin:0 0 25px;">
                                            <tr>
                                                <td style="padding:16px 20px;">
                                                    <p style="margin:0 0 10px;font-size:13px;font-weight:700;color:#d68910;">
                                                        Important — Please Read
                                                    </p>
                                                    <ul style="margin:0;padding-left:18px;color:#7d6608;
                                                               font-size:13px;line-height:1.9;">
                                                        <li>Keep your code <strong>{{APPLICATION_CODE}}</strong> safe for all correspondence.</li>
                                                        <li>Arrive at least <strong>15 minutes</strong> before your appointment.</li>
                                                        <li>Bring the <strong>original birth certificate</strong> and a photocopy.</li>
                                                        <li>Bring <strong>2 recent passport photographs</strong> of the applicant.</li>
                                                    </ul>
                                                </td>
                                            </tr>
                                        </table>
                
                                        <p style="color:#555;font-size:13px;line-height:1.7;margin:0;">
                                            For enquiries contact the school directly quoting your application code.
                                        </p>
                
                                    </td>
                                </tr>
                
                                <!-- Footer -->
                                <tr>
                                    <td style="background:#f8f9fa;border-top:1px solid #ecf0f1;
                                               padding:20px 40px;text-align:center;">
                                        <p style="margin:0;font-size:11px;color:#95a5a6;">
                                            This is an automated message. Please do not reply to this email.
                                        </p>
                                        <p style="margin:5px 0 0;font-size:12px;font-weight:700;color:#2980b9;">
                                            {{INSTITUTION_NAME}} &copy; {{YEAR}}
                                        </p>
                                    </td>
                                </tr>
                
                            </table>
                        </td>
                    </tr>
                </table>
                </body>
                </html>
                """;
    }

    private String getAdmissionStatusTemplate() {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <meta name="x-apple-disable-message-reformatting">
                    <title>Admission Status</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f9;font-family:'Segoe UI',Arial,sans-serif;">
                
                <div style="display:none;max-height:0;overflow:hidden;mso-hide:all;">
                    {{INSTITUTION_NAME}} — Admission decision for {{APPLICATION_CODE}}: {{APPLICATION_STATUS}}
                </div>
                
                <table width="100%" cellpadding="0" cellspacing="0" border="0" style="background-color:#f4f6f9;">
                    <tr>
                        <td align="center" style="padding:30px 10px;">
                            <table width="600" cellpadding="0" cellspacing="0" border="0"
                                   style="background:#ffffff;border-radius:10px;overflow:hidden;
                                          box-shadow:0 2px 10px rgba(0,0,0,0.08);max-width:600px;width:100%;">
                
                                <!-- Header -->
                                <tr>
                                    <td style="background:linear-gradient(135deg,#1a3c5e,#2980b9);
                                               padding:30px 40px;text-align:center;">
                                        <h1 style="color:#ffffff;margin:0;font-size:22px;
                                                   font-weight:700;letter-spacing:1px;">
                                            {{INSTITUTION_NAME}}
                                        </h1>
                                        <p style="color:rgba(255,255,255,0.85);margin:8px 0 0;font-size:13px;">
                                            {{APPLICATION_TYPE}} Admissions Decision
                                        </p>
                                    </td>
                                </tr>
                
                                <!-- Status Bar -->
                                <tr>
                                    <td style="background:{{STATUS_BAR_BG}};padding:10px;text-align:center;">
                                        <p style="color:#ffffff;margin:0;font-size:13px;
                                                  font-weight:600;letter-spacing:1px;text-transform:uppercase;">
                                            {{STATUS_BAR_TEXT}}
                                        </p>
                                    </td>
                                </tr>
                
                                <!-- Body -->
                                <tr>
                                    <td style="padding:35px 40px;">
                
                                        <p style="color:#2c3e50;font-size:15px;margin:0 0 20px;">
                                            Dear <strong>{{PARENT_NAME}}</strong>,
                                        </p>
                                        <p style="color:#555;font-size:14px;line-height:1.7;margin:0 0 25px;">
                                            {{OUTCOME_MESSAGE}}
                                        </p>
                
                                        <!-- Details Box -->
                                        <table width="100%" cellpadding="0" cellspacing="0" border="0"
                                               style="background:#f8fafc;border-radius:8px;
                                                      border-left:4px solid {{STATUS_COLOR}};margin:0 0 25px;">
                                            <tr>
                                                <td style="padding:20px 25px;">
                                                    <p style="margin:0 0 5px;font-size:11px;color:#7f8c8d;
                                                              text-transform:uppercase;letter-spacing:1px;">
                                                        Application Code
                                                    </p>
                                                    <p style="margin:0 0 15px;font-size:20px;font-weight:700;color:#1a3c5e;">
                                                        {{APPLICATION_CODE}}
                                                    </p>
                                                    <table width="100%" cellpadding="6" cellspacing="0">
                                                        <tr>
                                                            <td style="font-size:12px;color:#7f8c8d;width:45%;">Applicant Name</td>
                                                            <td style="font-size:13px;color:#2c3e50;font-weight:600;">{{APPLICANT_NAME}}</td>
                                                        </tr>
                                                        <tr style="background:#f0f4f8;">
                                                            <td style="font-size:12px;color:#7f8c8d;">Application Type</td>
                                                            <td style="font-size:13px;color:#2c3e50;font-weight:600;">{{APPLICATION_TYPE}}</td>
                                                        </tr>
                                                        <tr>
                                                            <td style="font-size:12px;color:#7f8c8d;">Application Date</td>
                                                            <td style="font-size:13px;color:#2c3e50;font-weight:600;">{{APPLICATION_DATE}}</td>
                                                        </tr>
                                                        <tr style="background:#f0f4f8;">
                                                            <td style="font-size:12px;color:#7f8c8d;">Decision</td>
                                                            <td style="font-size:13px;font-weight:700;color:{{STATUS_COLOR}};">
                                                                {{APPLICATION_STATUS}}
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                
                                        <!-- Next Steps / What Happens Next -->
                                        {{NEXT_STEPS}}
                
                                        <p style="color:#555;font-size:13px;line-height:1.7;margin:0;">
                                            For enquiries contact the school directly quoting your application code
                                            <strong>{{APPLICATION_CODE}}</strong>.
                                        </p>
                
                                    </td>
                                </tr>
                
                                <!-- Footer -->
                                <tr>
                                    <td style="background:#f8f9fa;border-top:1px solid #ecf0f1;
                                               padding:20px 40px;text-align:center;">
                                        <p style="margin:0;font-size:11px;color:#95a5a6;">
                                            This is an automated message. Please do not reply to this email.
                                        </p>
                                        <p style="margin:5px 0 0;font-size:12px;font-weight:700;color:#2980b9;">
                                            {{INSTITUTION_NAME}} &copy; {{YEAR}}
                                        </p>
                                    </td>
                                </tr>
                
                            </table>
                        </td>
                    </tr>
                </table>
                </body>
                </html>
                """;
    }
}
