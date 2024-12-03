package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.GradingSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradingSettingRepository extends JpaRepository<GradingSetting,Long> {
}
