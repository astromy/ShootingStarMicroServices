package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.model.Students;
import com.astromyllc.shootingstar.adminpta.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudentUtil {

    private final StudentRepository studentRepository;
    public static List<Students> studentsGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    private void fetAllStudents() {
        studentsGlobalList = studentRepository.findAll();
        log.info("Global Students List populated with {} records", studentsGlobalList.size());
    }
}
