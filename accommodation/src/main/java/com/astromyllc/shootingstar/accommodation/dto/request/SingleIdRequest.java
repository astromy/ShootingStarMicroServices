package com.astromyllc.shootingstar.accommodation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mirrors SingleStringRequest's pattern, for the handful of endpoints that
// only need a single numeric ID (idBlock, idBlockMaster, idBlockRoom, etc.)
// rather than a string value.
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SingleIdRequest {
    private Long val;
}
