package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.*;
import com.astromyllc.shootingstar.setup.dto.response.ClassesResponse;
import com.astromyllc.shootingstar.setup.model.Classes;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.repository.ClassesRepository;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.ClassesServiceInterface;
import com.astromyllc.shootingstar.setup.utils.ClassesUtil;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClassService implements ClassesServiceInterface {
    private final ClassesRepository classesRepository;
    private final ClassesUtil classesUtil;
    private final InstitutionUtils institutionUtils;
    private final InstitutionRepository institutionRepository;
    @Override
    public void createClass(ClassesRequest classesRequest) {

    }

    @Override
    public List<Optional<ClassesResponse>> createClasses(ClassesRequest classesRequestList) {
        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(classesRequestList.getInstitution())) // Filter by BECE code
                .findFirst() // Find the first matching institution
                .map(inst -> {
                    // Ensure the class list is not null, then process it directly
                    inst.setClassList(Optional.ofNullable(inst.getClassList()).orElse(new ArrayList<>()));

                    // Convert existing class names into a Set for quick lookup
                    Set<String> existingClasses = inst.getClassList().stream()
                            .map(c -> c.getName().toLowerCase())
                            .collect(Collectors.toSet());

                    // Filter out existing classes and add only new ones
                 List<Classes> newClasses=
                            classesRequestList.getClassDetailList().stream()
                                    .map(c -> {
                                        Classes mappedClass = ClassesUtil.mapClassRequestToClass(c);
                                        mappedClass.setInstitution(inst);  // Set the institution reference
                                        return mappedClass;
                                    })
                                    .filter(c -> !existingClasses.contains(c.getName().toLowerCase()))
                                    .toList();

                    // Save the updated institution with the new class list
                    classesRepository.saveAll(newClasses);
                    inst.getClassList().addAll(newClasses);

                    // Return the list of class responses wrapped in Optional
                    return inst.getClassList().stream()
                            .map(ClassesUtil::mapClassToClassResponse)
                            .collect(Collectors.toList());
                })
                .map(ArrayList::new) // Collect into a List<Optional<ClassesResponse>>
                .orElseGet(() -> {
                    log.warn("Institution not found!");
                    return new ArrayList<Optional<ClassesResponse>>(); // Return an empty list if the institution is not found
                });
    }

    @Override
    public List<Optional<ClassesResponse>> getAllClasses() {
        return ClassesUtil.classesGlobalList.stream().map(ClassesUtil::mapClassToClassResponse).toList();
    }

    @Override
    public List<Optional<ClassesResponse>> getAllClassesByClassGroup(ClassGroupRequest classGroupRequest) {
      return  InstitutionUtils.institutionGlobalList.stream()
              .filter(i->i.getBececode().equalsIgnoreCase(classGroupRequest.getInstitution()))
              .findFirst().get().getClassList().stream()
              .filter(f->f.getClassGroup().equalsIgnoreCase(classGroupRequest.getClassGroup()))
              .map(ClassesUtil::mapClassToClassResponse).toList();
    }

    @Override
    public List<Optional<ClassesResponse>> getAllClassesByInstitution(SingleStringRequest institutionRequest) {
        String finalBeceCode= institutionRequest.getVal();
        List<Optional<ClassesResponse>> classesResponse= InstitutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equalsIgnoreCase(finalBeceCode)).findFirst()
                .map(Institution::getClassList)  // Safe check if inst is null
                .map(Collection::stream)  // Safe check if getClassList() is null
                .map(stream -> stream.map(ClassesUtil::mapClassToClassResponse))  // Safe map operation
                .map(Stream::toList)  // Convert stream to list safely
                .orElse(Collections.emptyList());
        log.debug("\n\n\n\n List OF Classes..... {}",classesResponse );
        return classesResponse;
    }
}
