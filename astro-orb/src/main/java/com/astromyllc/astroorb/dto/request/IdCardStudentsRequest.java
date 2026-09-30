package com.astromyllc.astroorb.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Which classes to load students for. There is deliberately no institution
 * field: the school is always taken from the signed-in user's token.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class IdCardStudentsRequest {
    private List<String> classNames;
}
