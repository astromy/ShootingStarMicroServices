package com.astromyllc.shootingstar.accommodation.serviceInterface;

import com.astromyllc.shootingstar.accommodation.dto.request.BlockPrefectRequest;
import com.astromyllc.shootingstar.accommodation.dto.response.BlockPrefectResponse;

import java.util.List;

public interface BlockPrefectServiceInterface {
    BlockPrefectResponse assignBlockPrefect(Long idBlock, BlockPrefectRequest request);

    // Same pattern as BlockMaster - sets exitDate rather than deleting, to
    // preserve appointment history.
    BlockPrefectResponse endBlockPrefectAssignment(Long idBlockPrefect);

    List<BlockPrefectResponse> getBlockPrefects(Long idBlock);
}
