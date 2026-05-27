package com.astromyllc.onlineapplications.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DynamicStringRequest {
    private List<String> val;
    private List<String> key;
}
