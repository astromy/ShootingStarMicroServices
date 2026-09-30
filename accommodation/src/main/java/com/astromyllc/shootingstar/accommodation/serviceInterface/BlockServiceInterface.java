package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.BlockRoomRequest;
import com.astromyllc.shootingstar.accommodation.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockResponse;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockRoomResponse;

import java.util.List;

public interface BlockServiceInterface {
    BlockResponse addInstitutionAccommodation(BlockRequest blockRequest);

    // Changed from a single BlockResponse to a list - a school has multiple
    // dorm blocks (boys', girls', by year group, etc.), and the original
    // signature could only ever have represented one.
    List<BlockResponse> getInstitutionAccommodation(SingleStringRequest request);

    BlockResponse updateBlock(BlockRequest blockRequest);

    boolean deleteBlock(Long idBlock);

    BlockRoomResponse addRoomToBlock(Long idBlock, BlockRoomRequest roomRequest);

    boolean deleteRoom(Long idBlockRoom);
}
