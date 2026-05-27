package com.astromyllc.shootingstar.setup.utils;

import com.astromyllc.shootingstar.setup.dto.request.ClassDetail;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;
import com.astromyllc.shootingstar.setup.model.Classes;
import com.astromyllc.shootingstar.setup.model.Lookup;
import com.astromyllc.shootingstar.setup.repository.ClassesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClassesUtil {
    private static final LookupUtil lookupUtil = null;
    public static List<Classes> classesGlobalList = null;
    private final ClassesRepository classesRepository;

    public static Classes mapClassRequestToClass(ClassDetail c) {
        Lookup classGroupLookup = null;

        if (c.getClassGroup() != null) {
            // Find the lookup from the global list or repository
            classGroupLookup = lookupUtil.lookupGlobalList.stream().filter(cg -> cg.getIdLookup()
                            .equals(Long.parseLong(c.getClassGroup())))
                    .findFirst()
                    .orElse(null);
        }
        Classes c2 = Classes.builder()
                .classGroup(classGroupLookup)
                .name(c.getName())
                .build();
        return c2;
    }

    public static Classes mapClassRequestToClass(ClassDetail cr, Classes c) {
        Lookup classGroupLookup = null;

        if (c.getClassGroup() != null) {
            // Find the lookup from the global list or repository
            classGroupLookup = lookupUtil.lookupGlobalList.stream().filter(cg -> cg.getIdLookup()
                            .equals(Long.parseLong(cr.getClassGroup())))
                    .findFirst()
                    .orElse(null);
        }
        c.setClassGroup(classGroupLookup);
        c.setName(cr.getName());
        return c;
    }

    public static Optional<ClassesResponse> mapClassToClassResponse(Classes c) {
        return Optional.ofNullable(ClassesResponse.builder()
                .id(c.getIdClasses())
                .classGroup(c.getClassGroup() != null ? c.getClassGroup().getName() : null)
                .name(c.getName())
                .build());
    }

    @PostConstruct
    private void getAllClasses() {
        classesGlobalList = classesRepository.findAll();
        log.info("Global List of Classes Populated with {} records", classesGlobalList.stream().count());
    }

}
