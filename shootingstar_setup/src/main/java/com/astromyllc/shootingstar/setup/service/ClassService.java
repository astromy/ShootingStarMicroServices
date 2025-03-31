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
    public Optional<List<Optional<ClassesResponse>>> createClasses(ClassesRequest classesRequestList) {
        Optional<Institution> inst = InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(classesRequestList.getInstitution()))
                .findFirst();

        if (inst.isEmpty()) {
            log.warn("Institution not found!");
            return Optional.empty();
        }

        List<Classes> cl = inst.get().getClassList();
        if (cl == null) {
            cl = new ArrayList<>();
        }

        // Convert existing classes into a Set for quick lookup
        Set<String> existingClasses = cl.stream()
                .map(c -> c.getName().toLowerCase()) // Unique key: class name
                .collect(Collectors.toSet());

        // Filter out classes that already exist
        List<Classes> newClasses = classesRequestList.getClassDetailList().stream()
                .map(ClassesUtil::mapClassRequestToClass)
                .filter(c -> !existingClasses.contains(c.getName().toLowerCase()))
                .toList();

        // Add only unique classes
        cl.addAll(newClasses);
        inst.get().setClassList(cl);

        institutionRepository.save(inst.get());

        Optional<List<Optional<ClassesResponse>>> cr = Optional.of(
                inst.get().getClassList().stream()
                        .map(ClassesUtil::mapClassToClassResponse)
                        .toList()
        );

        return cr;
    }

    @Override
    public List<Optional<ClassesResponse>> getAllClasses() {
        return ClassesUtil.classesGlobalList.stream().map(ClassesUtil::mapClassToClassResponse).toList();
    }

    @Override
    public Optional<List<Optional<ClassesResponse>>> getAllClassesByClassGroup(ClassGroupRequest classGroupRequest) {
      return  Optional.of(InstitutionUtils.institutionGlobalList.stream()
              .filter(i->i.getBececode().equalsIgnoreCase(classGroupRequest.getInstitution()))
              .findFirst().get().getClassList().stream()
              .filter(f->f.getClassGroup().equalsIgnoreCase(classGroupRequest.getClassGroup()))
              .map(ClassesUtil::mapClassToClassResponse).toList());
    }

    @Override
    public List<Optional<ClassesResponse>> getAllClassesByInstitution(SingleStringRequest institutionRequest) {
        String finalBeceCode= institutionRequest.getVal();
        Optional<Institution> inst= InstitutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equalsIgnoreCase(finalBeceCode)).findFirst();
        //return Optional.ofNullable(inst.get().getClassList().stream().map(i->classesUtil.mapClassToClassResponse(i)).toList());
        return Optional.of(inst.get())
                .map(Institution::getClassList)  // Safe check if inst is null
                .map(Collection::stream)  // Safe check if getClassList() is null
                .map(stream -> stream.map(ClassesUtil::mapClassToClassResponse))  // Safe map operation
                .map(Stream::toList)  // Convert stream to list safely
                .orElse(Collections.emptyList());
    }
}
