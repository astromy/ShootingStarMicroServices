package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject,Long> {
}
