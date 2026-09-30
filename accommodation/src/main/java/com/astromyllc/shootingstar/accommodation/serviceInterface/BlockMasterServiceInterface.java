package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockMasterRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockMasterResponse;

import java.util.List;

public interface BlockMasterServiceInterface {
    BlockMasterResponse assignBlockMaster(Long idBlock, BlockMasterRequest request);

    // Sets exitDate rather than deleting the record, so the appointment
    // history for a block (who was in charge, and for how long) is kept.
    BlockMasterResponse endBlockMasterAssignment(Long idBlockMaster);

    List<BlockMasterResponse> getBlockMasters(Long idBlock);
}
