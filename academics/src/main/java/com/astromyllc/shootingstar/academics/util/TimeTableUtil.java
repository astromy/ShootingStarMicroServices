package com.astromyllc.shootingstar.academics.util;

import com.astromyllc.shootingstar.academics.model.TimeTable;
import com.astromyllc.shootingstar.academics.repository.TimeTableRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TimeTableUtil {

    private final TimeTableRepository timeTableRepository;
    public static List<TimeTable> timeTablesGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    private void fetAllTimeTable() {
        timeTablesGlobalList = timeTableRepository.findAll();
        log.info("Global TimeTable List populated with {} records", timeTablesGlobalList.size());
    }

}
