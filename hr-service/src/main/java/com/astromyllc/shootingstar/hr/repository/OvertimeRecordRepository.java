package com.astromyllc.shootingstar.hr.repository;

import com.astromyllc.shootingstar.hr.model.OvertimeRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface OvertimeRecordRepository extends MongoRepository<OvertimeRecord, String> {
    List<OvertimeRecord> findByInstitutionCode(String institutionCode);

    List<OvertimeRecord> findByInstitutionCodeAndPayrollRefIn(String institutionCode, Collection<String> payrollRefs);
}
