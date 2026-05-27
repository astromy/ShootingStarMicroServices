package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.ApplicationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationCategoryRepository extends JpaRepository<ApplicationCategory,Long> {
}
