package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTableRepository extends JpaRepository<TimeTable,Long> {
}
