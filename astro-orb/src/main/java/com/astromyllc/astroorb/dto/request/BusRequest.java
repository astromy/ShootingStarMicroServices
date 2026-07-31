package com.astromyllc.astroorb.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BusRequest {
    @NonNull
    private String institution;
    private List<BusDetails> busDetailsList;
}