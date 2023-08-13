package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;
import com.astromyllc.shootingstar.setup.model.Classes;
import com.astromyllc.shootingstar.setup.repository.ClassesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClassesUtil {
    private final ClassesRepository classesRepository;
    public static List<Classes> classesGlobalList=null;
    @Bean
    private void getAllClasses(){
       classesGlobalList=classesRepository.findAll();
        log.info("Global List of Classes Populated with {} records",classesGlobalList.stream().count());
    }

    public static Classes mapClassRequestToClass(ClassesRequest c) {
        return Classes.builder()
                .classGroup(c.getClassGroup())
                .name(c.getName())
                .build();
    }
    public static ClassesResponse mapClassToClassResponse(Classes c) {
        return ClassesResponse.builder()
                .id(c.getIdClasses())
                .classGroup(c.getClassGroup())
                .name(c.getName())
                .build();
    }

}
