package com.astromyllc.shootingstar.com.astromyllc.shootingstar.astroauthauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sequence_generator")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeqGenerator {
    @Id
    private int seqNo;
    private String type;
}
