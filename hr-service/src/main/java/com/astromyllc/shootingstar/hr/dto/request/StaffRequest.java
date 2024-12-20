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
@Embeddable
public class StaffRequest {
    private String id;
    @Nonnull
    private String staffCode;
    @Nonnull
    private String firstNames;
    @Nonnull
    private String lastName;
    @Nonnull
    private String dateOfBirth;
    @Nonnull
    private String nationality;
    private String homeTown;
    @Nonnull
    private String residentialTown;
    @Nonnull
    private String contact1;
    @Nonnull
    private String backupContact;
    @Nonnull
    private String nationalIDType;
    @Indexed(unique = true)
    @Nonnull
    private String nationalID;
    @Indexed(unique = true)
    @Nonnull
    private String snnitNumber;
    @Nonnull
    private String maritalStatus;
    private String nameOfSpouse;
    @Nonnull
    private String dateOfEmployment;
    @Nonnull
    private String gender;
    private String level;
    private String designation;
    @Nonnull
    private String staffPicture;
    @Nonnull
    private String nextOfKing;
    @Nonnull
    private String institutionCode;
    //private List<PortfolioRequest> portfolio;
    private List<DependantsRequest> dependants;
    private List<AcademicRecordsRequest> academicRecords;
    private List<ProfessionalRecordsRequest> professionalRecords;
    private List<StaffDocumentsRequest> staffDocuments;


}
