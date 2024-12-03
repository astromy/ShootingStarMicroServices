package com.astromyllc.shootingstar.setup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "jobdescription")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Embeddable
public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJobDescription;
    private String jobDescription;
}
