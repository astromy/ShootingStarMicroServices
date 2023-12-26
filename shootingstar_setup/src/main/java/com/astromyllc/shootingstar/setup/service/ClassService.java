package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.ClassDetail;
import com.astromyllc.shootingstar.setup.dto.request.ClassGroupRequest;
import com.astromyllc.shootingstar.setup.dto.request.ClassesRequest;
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
    public void createClasses(ClassesRequest classesRequestList) {
        Optional<Institution> inst=institutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equals(classesRequestList.getInstitution())).findFirst();
       List<ClassDetail> cl=classesRequestList.getClassDetailList().stream().filter(ci->ci.getId()==null).collect(Collectors.toList());
       if(cl.isEmpty()){
         List<Classes> cl1= (List<Classes>) classesRequestList.getClassDetailList().stream().map(c -> classesUtil.mapClassRequestToClass(c, (Classes) classesUtil.classesGlobalList.stream().filter(x->x.getIdClasses().equals(c.getId()))));
       classesRepository.saveAll(cl1);
       }else {
           inst.get().setClassList((List<Classes>) cl.stream().map(c -> classesUtil.mapClassRequestToClass(c)));
           institutionRepository.save(inst.get());
       }
    }

    @Override
    public List<Optional<ClassesResponse>> getAllClasses() {
        List<Optional<ClassesResponse>> classList= classesUtil.classesGlobalList.stream().map(c->classesUtil.mapClassToClassResponse(c)).collect(Collectors.toList());
        return classList;
    }

    @Override
    public List<Optional<ClassesResponse>> getAllClassesByClassGroup(ClassGroupRequest classGroupRequest) {
      return  institutionUtils.institutionGlobalList.stream()
              .filter(i->i.getBececode().equals(classGroupRequest.getInstitution()))
              .findFirst().get().getClassList().stream()
              .filter(f->f.getClassGroup().equals(classGroupRequest.getClassGroup()))
              .map(c->classesUtil.mapClassToClassResponse(c)).collect(Collectors.toList());
    }

    @Override
    public Optional<List<Optional<ClassesResponse>>> getAllClassesByInstitution(String institutionRequest) {
        String finalBeceCode= institutionRequest.split("\"")[3];
        Optional<Institution> inst=institutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equals(institutionRequest)).findFirst();
        return Optional.ofNullable(inst.get().getClassList().stream().map(i->classesUtil.mapClassToClassResponse(i)).collect(Collectors.toList()));
    }
}
