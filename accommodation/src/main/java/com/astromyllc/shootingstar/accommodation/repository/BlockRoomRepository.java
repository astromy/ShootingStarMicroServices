package com.astromyllc.shootingstar.accommodation.repository;

import com.astromyllc.shootingstar.accommodation.model.BlockRoom;
import org.springframework.data.jpa.repository.JpaRepository;

// Rooms are owned by Block (see Block.roomsList, cascade = ALL), so most
// writes happen by saving the parent Block. This repository exists for
// direct reads - e.g. finding a specific room by ID when assigning a
// student to it, without needing to load and search through the whole
// parent Block first.
public interface BlockRoomRepository extends JpaRepository<BlockRoom, Long> {
}
