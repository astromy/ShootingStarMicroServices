package com.astromyllc.shootingstar.academics.repository;

import com.astromyllc.shootingstar.academics.model.PublishedReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublishedReportRepository extends JpaRepository<PublishedReport, Long> {
}