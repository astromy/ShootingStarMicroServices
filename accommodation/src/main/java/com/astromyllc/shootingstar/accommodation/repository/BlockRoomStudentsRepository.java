package com.astromyllc.shootingstar.accommodation.repository;

import com.astromyllc.shootingstar.accommodation.model.BlockRoomStudents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRoomStudentsRepository extends JpaRepository<BlockRoomStudents, Long> {
    List<BlockRoomStudents> findByBlockRoom_IdBlockRoom(Long idBlockRoom);

    Optional<BlockRoomStudents> findByStudentIDAndInstitutionID(String studentID, String institutionID);

    List<BlockRoomStudents> findByInstitutionID(String institutionID);
}
