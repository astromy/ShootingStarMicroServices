package com.astromyllc.shootingstar.clinic.util;

import com.astromyllc.shootingstar.clinic.model.VitalRecords;
import com.astromyllc.shootingstar.clinic.repository.VitalRecordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VitalRecordsUtil {
    private final VitalRecordsRepository vitalRecordsRepository;

    public static List<VitalRecords> vitalRecordsList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetAllVitalRecords() {
        vitalRecordsList = vitalRecordsRepository.findAll();
        log.info("Global Vital Records List populated with {} records", vitalRecordsList.size());
    }
}
