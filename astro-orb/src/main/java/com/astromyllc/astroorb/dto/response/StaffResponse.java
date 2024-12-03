package com.astromyllc.astroorb.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StaffResponse {
    private String id;
    private String staffCode;
    private String firstNames;
    private String lastName;
    private LocalDate dateOfBirth;
    private String nationality;
    private String homeTown;
    private String residentialTown;
    private String contact1;
    private String backupContact;
    private String nationalIDType;
    private String nationalID;
    private String snnitNumber;
    private String maritalStatus;
    private String nameOfSpouse;
    private LocalDate dateOfEmployment;
    private String gender;
    private String level;
    private String designation;
    private String staffPicture;
    private String nextOfKing;
    private String institutionCode;
   // private List<PortfolioRequest> portfolio;
    private List<DependantsResponse> dependants;
    private List<AcademicRecordsResponse> academicRecords;
    private List<ProfessionalRecordsResponse> professionalRecords;
    private List<StaffDocumentsResponse> staffDocuments;


}
