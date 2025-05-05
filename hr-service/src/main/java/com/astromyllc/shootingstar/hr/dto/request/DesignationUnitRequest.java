package com.astromyllc.shootingstar.hr.dto.request;

import com.astromyllc.shootingstar.hr.model.Portfolio;
import jakarta.persistence.*;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DesignationUnitRequest {
    private ObjectId id;
    private String unitName;
    private String designation;
    private String institutionCode;

}
