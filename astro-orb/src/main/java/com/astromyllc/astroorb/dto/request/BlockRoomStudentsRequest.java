package com.astromyllc.astroorb.dto.request;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlockRoomStudentsRequest {
    private Long idBlockRoomStudent;
    private String studentID;
    private String institutionID;

    @ManyToOne
    @JoinColumn(name = "idBlockRoom")
    private BlockRoomRequest blockRoom;
}
