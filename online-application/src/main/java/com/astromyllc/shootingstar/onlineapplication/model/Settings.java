package com.astromyllc.shootingstar.onlineapplication.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Document(value = "settings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Settings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private ObjectId id;
    private String applicationFormType;
    private int applicationFormQNT;
    private double paymentAmount;
    private String paymentMedium;
    private long institutionId;
    private LocalDateTime commencement;
    private LocalDateTime closure;
    private int appointmentPerDay;
    private LocalDateTime appointmentCommencement;
    private LocalDateTime appointmentClosure;


}
