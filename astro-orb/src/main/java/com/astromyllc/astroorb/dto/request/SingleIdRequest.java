package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mirrors SingleStringRequest's pattern for the accommodation endpoints
// that need a single numeric ID (idBlock, idBlockMaster, idBlockRoom, etc.)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SingleIdRequest {
    private Long val;
}
