package com.astromyllc.shootingstar.onlineapplication.utils;

import com.astromyllc.shootingstar.onlineapplication.dto.request.ApplicationRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.request.InstitutionRequest;
import com.astromyllc.shootingstar.onlineapplication.dto.response.ApplicationsResponse;
import com.astromyllc.shootingstar.onlineapplication.dto.response.alien.ParentsResponse;
import com.astromyllc.shootingstar.onlineapplication.dto.response.alien.ProcessedApplicationResponse;
import com.astromyllc.shootingstar.onlineapplication.model.Applications;
import com.astromyllc.shootingstar.onlineapplication.model.Appointment;
import com.astromyllc.shootingstar.onlineapplication.repository.ApplicationsRepository;
import com.astromyllc.shootingstar.onlineapplication.repository.AppointmentRepository;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ApplicationUtilities {


    private final ApplicationsRepository applicationsRepository;
    private final WebClient.Builder webClientBuilder;
    private static Long applicantIndex = 0L;
    public static List<Applications> apl = null;
    private InstitutionRequest institutionRequest = null;

    private final AppointmentRepository appointmentRepository;
    public static List<Appointment> appointmentsGlobal = null;
    @Value("${gateway.host}")
    private String host;

    @Bean
    public void appointmentList() {
        log.info("Appointment List Fetched");
        appointmentsGlobal = appointmentRepository.findAll().stream().filter(x -> x.getAppointmentDateTime().getYear() == LocalDate.now().getYear()).collect(Collectors.toList());
    }

    @Bean
    public void applicationsList() {
        log.info("Application List Fetched");
        apl = applicationsRepository.findAll().stream().filter(x -> x.getApplicationDate().getYear() == LocalDate.now().getYear()).collect(Collectors.toList());
    }

    @Bean
    File filesystemRoot() throws URISyntaxException {
        return Paths.get((getClass().getClassLoader().getResource("static/applicationDocuments/Pictures/")).toURI()).toFile();
    }

    @Bean
    File docfilesystemRoot() throws URISyntaxException {
        return Paths.get((getClass().getClassLoader().getResource("static/applicationDocuments/BirthCerts/")).toURI()).toFile();
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ApplicationsResponse mapApplications_ToApplicationResponse(Applications applications1) {
        return ApplicationsResponse.builder()
                .idapplication(applications1.getIdapplication())
                .fatherFirstNames(applications1.getFatherFirstNames())
                .fatherLastName(applications1.getFatherLastName())
                .fatherEmail(applications1.getFatherEmail())
                .fatherOccupation(applications1.getFatherOccupation())
                .fatherContact1(applications1.getFatherContact1())
                .fatherContact2(applications1.getFatherContact2())
                .fatherPlaceOfWork(applications1.getFatherPlaceOfWork())

                .motherEmail(applications1.getMotherEmail())
                .motherContact1(applications1.getMotherContact1())
                .motherContact2(applications1.getMotherContact2())
                .motherFirstNames(applications1.getMotherFirstNames())
                .motherLastName(applications1.getMotherLastName())
                .motherOccupation(applications1.getMotherOccupation())
                .motherPlaceOfWork(applications1.getMotherPlaceOfWork())

                .applicantOtherName(applications1.getApplicantOtherName())
                .applicantFirstName(applications1.getApplicantFirstName())
                .applicantLastName(applications1.getApplicantLastName())
                .applicantGender(applications1.getApplicantGender())

                .applicantDenomination(applications1.getApplicantDenomination())
                .applicantNationality(applications1.getApplicantNationality())
                .applicantPicture(applications1.getApplicantPicture())
                .applicantCountryOfBirth(applications1.getApplicantCountryOfBirth())
                .applicantPlaceOfBirth(applications1.getApplicantPlaceOfBirth())
                .applicantDateOfBirth(applications1.getApplicantDateOfBirth())
                .applicantBirthCert(applications1.getApplicantBirthCert())

                .applicationCode(applications1.getApplicationCode())
                .applicationDate(applications1.getApplicationDate())
                .applicationInstitution(applications1.getApplicationInstitution())
                .applicationStatus(applications1.getApplicationStatus())
                .appointmentDate(applications1.getAppointmentDate())
                .applicationType(applications1.getApplicationType())

                .nameOfPreviousSchool(applications1.getNameOfPreviousSchool())
                .addressOfPreviousSchool(applications1.getAddressOfPreviousSchool())
                .classOfDeparture(applications1.getClassOfDeparture())
                .contactOfPreviousSchool(applications1.getContactOfPreviousSchool())
                .reasonForDeparture(applications1.getReasonForDeparture())
                .build();
    }

    public ApplicationsResponse mapApplications_ToApplicationResponseWithImage(Applications applications1) {
        return ApplicationsResponse.builder()
                .idapplication(applications1.getIdapplication())
                .fatherFirstNames(applications1.getFatherFirstNames())
                .fatherLastName(applications1.getFatherLastName())
                .fatherEmail(applications1.getFatherEmail())
                .fatherOccupation(applications1.getFatherOccupation())
                .fatherContact1(applications1.getFatherContact1())
                .fatherContact2(applications1.getFatherContact2())
                .fatherPlaceOfWork(applications1.getFatherPlaceOfWork())

                .motherEmail(applications1.getMotherEmail())
                .motherContact1(applications1.getMotherContact1())
                .motherContact2(applications1.getMotherContact2())
                .motherFirstNames(applications1.getMotherFirstNames())
                .motherLastName(applications1.getMotherLastName())
                .motherOccupation(applications1.getMotherOccupation())
                .motherPlaceOfWork(applications1.getMotherPlaceOfWork())

                .applicantOtherName(applications1.getApplicantOtherName())
                .applicantFirstName(applications1.getApplicantFirstName())
                .applicantLastName(applications1.getApplicantLastName())
                .applicantGender(applications1.getApplicantGender())

                .applicantDenomination(applications1.getApplicantDenomination())
                .applicantNationality(applications1.getApplicantNationality())
                .applicantPicture(applications1.getApplicantPicture())
                .applicantCountryOfBirth(applications1.getApplicantCountryOfBirth())
                .applicantPlaceOfBirth(applications1.getApplicantPlaceOfBirth())
                .applicantDateOfBirth(applications1.getApplicantDateOfBirth())
                .applicantBirthCert(applications1.getApplicantBirthCert())

                .applicationCode(applications1.getApplicationCode())
                .applicationDate(applications1.getApplicationDate())
                .applicationInstitution(applications1.getApplicationInstitution())
                .applicationStatus(applications1.getApplicationStatus())
                .appointmentDate(applications1.getAppointmentDate())
                .applicationType(applications1.getApplicationType())

                .nameOfPreviousSchool(applications1.getNameOfPreviousSchool())
                .addressOfPreviousSchool(applications1.getAddressOfPreviousSchool())
                .classOfDeparture(applications1.getClassOfDeparture())
                .contactOfPreviousSchool(applications1.getContactOfPreviousSchool())
                .reasonForDeparture(applications1.getReasonForDeparture())
                .build();
    }

    public Applications mapApplicationRequest_ToApplications(ApplicationRequest applications1) throws IOException, URISyntaxException {
        String applicationCode = generateApplicationCode(applications1.getApplicationInstitution());
        String appointmentDate = setAppointmentDate(applicationCode, applications1.getApplicationType());
        if (appointmentDate==null) return null;
        String fp = filesystemRoot().getPath();
        String fd = docfilesystemRoot().getAbsolutePath();
        String filepath = saveFile(applications1.getApplicantPicture(), applicationCode, fp);
        String fileDocumentpath = saveFile(applications1.getApplicantBirthCert(), applicationCode, fd, applications1.getApplicantBirthCertFileType());
        return Applications.builder()
                .idapplication(applications1.getIdapplication())
                .fatherFirstNames(applications1.getFatherFirstNames())
                .fatherLastName(applications1.getFatherLastName())
                .fatherEmail(applications1.getFatherEmail())
                .fatherOccupation(applications1.getFatherOccupation())
                .fatherContact1(applications1.getFatherContact1())
                .fatherContact2(applications1.getFatherContact2())
                .fatherPlaceOfWork(applications1.getFatherPlaceOfWork())

                .motherEmail(applications1.getMotherEmail())
                .motherContact1(applications1.getMotherContact1())
                .motherContact2(applications1.getMotherContact2())
                .motherFirstNames(applications1.getMotherFirstNames())
                .motherLastName(applications1.getMotherLastName())
                .motherOccupation(applications1.getMotherOccupation())
                .motherPlaceOfWork(applications1.getMotherPlaceOfWork())

                .applicantOtherName(applications1.getApplicantOtherName())
                .applicantFirstName(applications1.getApplicantFirstName())
                .applicantLastName(applications1.getApplicantLastName())
                .applicantGender(applications1.getApplicantGender())

                .applicantDenomination(applications1.getApplicantDenomination())
                .applicantNationality(applications1.getApplicantNationality())
                .applicantPicture(filepath)
                .applicantCountryOfBirth(applications1.getApplicantCountryOfBirth())
                .applicantPlaceOfBirth(applications1.getApplicantPlaceOfBirth())
                .applicantDateOfBirth(applications1.getApplicantDateOfBirth())
                .applicantBirthCert(fileDocumentpath)

                .applicationCode(applicationCode)
                .applicationStatus(applications1.getApplicationStatus())
                .applicationDate(LocalDate.now())
                .applicationInstitution(applications1.getApplicationInstitution())
                .appointmentDate(LocalDateTime.parse(appointmentDate, formatter))
                .applicationType(applications1.getApplicationType())

                .nameOfPreviousSchool(applications1.getNameOfPreviousSchool())
                .addressOfPreviousSchool(applications1.getAddressOfPreviousSchool())
                .classOfDeparture(applications1.getClassOfDeparture())
                .contactOfPreviousSchool(applications1.getContactOfPreviousSchool())
                .reasonForDeparture(applications1.getReasonForDeparture())
                .build();
    }

    private String generateApplicationCode(String applicationInstitution) {
        JSONObject json = new JSONObject();
        json.put("beceCode", applicationInstitution);
        institutionRequest = webClientBuilder.build().post()
                .uri("http://"+host+":8083/api/setup/getInstitutionByCode")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(json), JSONObject.class)
                .retrieve()
                .bodyToMono(InstitutionRequest.class)
                .block();
        applicantIndex = apl
                .stream()
                .filter(x -> x.getApplicationDate().getYear() == LocalDate.now().getYear())
                .count() + 1;
        return String.valueOf(LocalDate.now().getYear()).substring(2) + "-" + applicationInstitution + "-" + applicantIndex;

    }

    private String setAppointmentDate(String code, String type) {
        String commencement = institutionRequest.getAdmissions().getApplicationCategoryList().stream().filter(x -> x.getApplicationFormType().equals(type)).findFirst().get().getAppointmentCommencement();
        int appointmentsPerDay = institutionRequest.getAdmissions().getApplicationCategoryList().stream().filter(x -> x.getApplicationFormType().equals(type)).findFirst().get().getAppointmentPerDay();
        int formsQNT = institutionRequest.getAdmissions().getApplicationCategoryList().stream().filter(x -> x.getApplicationFormType().equals(type)).findFirst().get().getApplicationFormQNT();
        String closure = institutionRequest.getAdmissions().getApplicationCategoryList().stream().filter(x -> x.getApplicationFormType().equals(type)).findFirst().get().getAppointmentClosure();
        Map<LocalDateTime, List<Appointment>> map = appointmentsGlobal.stream()
                .filter(x->x.getAppointmentDateTime().isAfter(LocalDateTime.parse(commencement.replace("T"," "),formatter))
                && x.getAppointmentDateTime().isBefore(LocalDateTime.parse(closure.replace("T"," "),formatter)))
                .collect(groupingBy(Appointment::getAppointmentDateTime));
        Appointment ap = new Appointment();
        Object o = map.keySet().toArray()[0];

        if (!code.isEmpty()) {
            if (!map.isEmpty()) {
                int appointmentArraySize = map.get(o).size();
                if (map.get(o).get(0).getAppointmentDateTime().isBefore(LocalDateTime.parse(closure.replace("T", " "), formatter)) && formsQNT<appointmentArraySize) {
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

    public String saveFile(String file, String fileName, String fileDirectoryPath, String fileTye) throws IOException {
        if (fileTye.equals("image")) {
            fileName = fileName + ".png";
        } else {
            fileName = fileName + ".pdf";
        }
        String fileLocation = fileDirectoryPath + "\\" + fileName;
        byte[] data = DatatypeConverter.parseBase64Binary(Arrays.toString(file.toCharArray()));
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileLocation));
        outputStream.write(data);
        return fileLocation;
    }

    public String saveFile(String file, String fileName, String fileDirectoryPath) throws IOException {
        fileName = fileName + ".png";
        String fileLocation = fileDirectoryPath + "\\" + fileName;
        byte[] data = DatatypeConverter.parseBase64Binary(Arrays.toString(file.toCharArray()));
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileLocation));
        outputStream.write(data);
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

    private List<ParentsResponse> parentResponseBuilder(Applications x){
        List<ParentsResponse> pr= new ArrayList<>();
        pr.add(ParentsResponse.builder()
                .email(x.getFatherEmail())
                .institutionCode(x.getApplicationInstitution())
                .parentType("Father")
                .contact1(x.getFatherContact1())
                .contact2(x.getFatherContact2())
                .placeOfWork(x.getFatherPlaceOfWork())
                .occupation(x.getFatherOccupation())
                .firstNames(x.getFatherFirstNames())
                .lastName(x.getFatherLastName())
                .build());

        pr.add(ParentsResponse.builder()
                .email(x.getMotherEmail())
                .institutionCode(x.getApplicationInstitution())
                .parentType("Mother")
                .contact1(x.getMotherContact1())
                .contact2(x.getMotherContact2())
                .placeOfWork(x.getMotherPlaceOfWork())
                .occupation(x.getMotherOccupation())
                .firstNames(x.getMotherFirstNames())
                .lastName(x.getMotherLastName())
                .build());
                return pr;
    }
}
