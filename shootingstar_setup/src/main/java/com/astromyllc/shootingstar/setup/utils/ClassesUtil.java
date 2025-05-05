package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.ClassDetail;
import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;
import com.astromyllc.shootingstar.setup.model.Classes;
import com.astromyllc.shootingstar.setup.repository.ClassesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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

    public static Classes mapClassRequestToClass(ClassDetail c) {
        Classes c2= Classes.builder()
                .classGroup(c.getClassGroup())
                .name(c.getName())
                .build();
        return c2;
    }


    public static Classes mapClassRequestToClass(ClassDetail cr,Classes c) {
        c.setClassGroup(cr.getClassGroup());
        c.setName(cr.getName());
        return c;
    }
    public static Optional<ClassesResponse> mapClassToClassResponse(Classes c) {
        return Optional.ofNullable(ClassesResponse.builder()
                .id(c.getIdClasses())
                .classGroup(c.getClassGroup())
                .name(c.getName())
                .build());
    }

}
