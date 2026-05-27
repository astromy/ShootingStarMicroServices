package com.astromyllc.shootingstar.setup.repository;

import com.astromyllc.shootingstar.setup.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
}
