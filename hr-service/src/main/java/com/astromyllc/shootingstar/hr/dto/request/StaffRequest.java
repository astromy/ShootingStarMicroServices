package com.astromyllc.shootingstar.hr.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StaffRequest {
    private String id;
    private String staffCode;
    private String firstNames;
    private String lastName;
    private String dateOfBirth;
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
    private String dateOfEmployment;
    private String gender;
    private String level;
    private String designation;
    private String staffPicture;
    private String nextOfKing;
    private String institutionCode;
    //private List<PortfolioRequest> portfolio;
    private List<DependantsRequest> dependants;
    private List<AcademicRecordsRequest> academicRecords;
    private List<ProfessionalRecordsRequest> professionalRecords;
    private List<StaffDocumentsRequest> staffDocuments;


}
