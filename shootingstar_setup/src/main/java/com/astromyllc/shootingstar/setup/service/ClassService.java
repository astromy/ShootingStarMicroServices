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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<Institution> inst= InstitutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equalsIgnoreCase(classesRequestList.getInstitution())).findFirst();
        List<Classes> cl=inst.get().getClassList();
        cl.addAll(classesRequestList.getClassDetailList().stream().map(ClassesUtil::mapClassRequestToClass).toList());
        inst.get().setClassList(cl);
        institutionRepository.save(inst.get());
        Optional<List<Optional<ClassesResponse>>> cr= Optional.ofNullable(inst.get().getClassList().stream().map(s->classesUtil.mapClassToClassResponse(s)).collect(Collectors.toList()));
        return cr;
    }

    @Override
    public List<Optional<ClassesResponse>> getAllClasses() {
        List<Optional<ClassesResponse>> classList= classesUtil.classesGlobalList.stream().map(c->classesUtil.mapClassToClassResponse(c)).collect(Collectors.toList());
        return classList;
    }

    @Override
    public Optional<List<Optional<ClassesResponse>>> getAllClassesByClassGroup(ClassGroupRequest classGroupRequest) {
      return  Optional.ofNullable(institutionUtils.institutionGlobalList.stream()
              .filter(i->i.getBececode().equalsIgnoreCase(classGroupRequest.getInstitution()))
              .findFirst().get().getClassList().stream()
              .filter(f->f.getClassGroup().equalsIgnoreCase(classGroupRequest.getClassGroup()))
              .map(c->classesUtil.mapClassToClassResponse(c)).collect(Collectors.toList()));
    }

    @Override
    public Optional<List<Optional<ClassesResponse>>> getAllClassesByInstitution(SingleStringRequest institutionRequest) {
        String finalBeceCode= institutionRequest.getVal();
        Optional<Institution> inst=institutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equalsIgnoreCase(finalBeceCode)).findFirst();
        return Optional.ofNullable(inst.get().getClassList().stream().map(i->classesUtil.mapClassToClassResponse(i)).collect(Collectors.toList()));
    }
}
