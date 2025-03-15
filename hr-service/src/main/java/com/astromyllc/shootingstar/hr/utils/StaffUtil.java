package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.hr.dto.request.StaffRequest;
import com.astromyllc.shootingstar.hr.dto.request.alien.InstitutionRequest;
import com.astromyllc.shootingstar.hr.dto.response.*;
import com.astromyllc.shootingstar.hr.model.Staff;
import com.astromyllc.shootingstar.hr.repository.StaffRepository;
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
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffUtil {
    private final StaffRepository staffRepository;
    private final WebClient.Builder webClientBuilder;
    public static List<Staff> staffGlobalList;
    private static Long staffIndex = 0L;
    private static InstitutionRequest institutionRequest = null;
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Value("${gateway.host}")
    private String host;

    private final ProfessionalRecordsUtil professionalRecordsUtil;
    private final StaffDocumentsUtil staffDocumentsUtil;
    private final DependantsUtil dependantsUtil;
    private final AcademicRecordsUtil academicRecordsUtil;

    @Bean
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
                /*webClientBuilder.build().post()
                .uri("http://"+host+"/api/setup/getInstitutionByCode")
                //.contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)//(Mono.just(json), JSONObject.class)
                .retrieve()
                .bodyToMono(InstitutionRequest.class)
                .block();*/

                WebClient.builder()
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
        staffIndex = staffGlobalList
                .stream()
                .filter(x -> x.getInstitutionCode().equalsIgnoreCase(institutionCode))
                .count() + 1;
        return "S" + institutionCode + "-" + staffIndex;

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
        staff.setStaffCode(staffRequest.getStaffCode());
        staff.setFirstNames(staffRequest.getFirstNames());
        staff.setLastName(staffRequest.getLastName());
        staff.setNationality(staffRequest.getNationality());
        staff.setHomeTown(staffRequest.getHomeTown());
        staff.setResidentialTown(staffRequest.getResidentialTown());
        staff.setContact1(staffRequest.getContact1());
        staff.setBackupContact(staffRequest.getBackupContact());
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
        List<DependantsResponse> dr =  staff.getDependants().stream().filter(d -> d.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode())).toList().stream().map(DependantsUtil::mapDependants_ToDependantResponse).toList();
        List<StaffDocumentsResponse> sdr= staff.getStaffDocuments().stream().filter(sd -> sd.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode())).toList().stream().map(StaffDocumentsUtil::mapStaffDocuments_ToStaffDocuentsResponse).toList();
        List<AcademicRecordsResponse> arr =staff.getAcademicRecords().stream().filter(d -> d.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode())).toList().stream().map(AcademicRecordsUtil::mapAcademicRecord_ToAcademicRecordsResponse).toList();
        List<ProfessionalRecordsResponse> prr =staff.getProfessionalRecords().stream().filter(d -> d.getInstitutionCode().equalsIgnoreCase(staff.getInstitutionCode())).toList().stream().map(ProfessionalRecordsUtil::mapProfessionalRecord_ToProfessionalRecordsResponse).toList();

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

}
