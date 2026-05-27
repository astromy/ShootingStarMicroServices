package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDescriptionRepository extends JpaRepository<JobDescription,Long> {
}
