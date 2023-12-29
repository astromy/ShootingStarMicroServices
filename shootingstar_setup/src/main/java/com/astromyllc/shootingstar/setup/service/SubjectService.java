package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.*;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
import com.astromyllc.shootingstar.setup.model.Classes;
import com.astromyllc.shootingstar.setup.model.Institution;
import com.astromyllc.shootingstar.setup.model.Subject;
import com.astromyllc.shootingstar.setup.repository.InstitutionRepository;
import com.astromyllc.shootingstar.setup.repository.SubjectRepository;
import com.astromyllc.shootingstar.setup.serviceInterface.SubjectServiceInterface;
import com.astromyllc.shootingstar.setup.utils.InstitutionUtils;
import com.astromyllc.shootingstar.setup.utils.SubjectUtil;
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
public class SubjectService implements SubjectServiceInterface {
    private final SubjectUtil subjectUtil;
    private final SubjectRepository subjectRepository;
    private final InstitutionUtils institutionUtils;
    private final InstitutionRepository institutionRepository;
    @Override
    public void createSubject(SubjectRequest subjectRequest) {
        Optional<Institution> inst=institutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equals(subjectRequest.getInstitution())).findFirst();
        List<SubjectDetails> sl=subjectRequest.getSubjectDetails().stream().filter(ci->ci.getId()==null).collect(Collectors.toList());
        if(sl.isEmpty()){
            List<Subject> sl1= (List<Subject>) subjectRequest.getSubjectDetails().stream().map(c -> subjectUtil.mapSubjectRequest_ToSubject(c, (Subject) subjectUtil.subjectGlobalList.stream().filter(x->x.getIdSubject().equals(c.getId()))));
            subjectRepository.saveAll(sl1);
        }else {
            inst.get().setClassList((List<Classes>) sl.stream().map(c -> subjectUtil.mapSubjectRequest_ToSubject(c)));
            institutionRepository.save(inst.get());
        }
    }

    @Override
    public void createSubjects(List<SubjectRequest> subjectRequestList) {

    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjects() {
       return subjectUtil.subjectGlobalList.stream().map(s->subjectUtil.mapSubject_ToSubjectResponse(s)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjectsByClass(ClassesRequest classesRequest) {
       return institutionUtils.institutionGlobalList.stream().filter(i->i.getIdInstitution().equals(classesRequest.getInstitution())).findFirst().get().getClassList()
                .stream().map(cl->classesRequest.getClassDetailList().stream().filter(c->c.getId().equals(cl.getIdClasses()))).map(s->subjectUtil.mapSubject_ToSubjectResponse((Subject) s)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjectsByInstitution(SingleStringRequest institutionRequest) {
        String finalBeceCode= institutionRequest.getVal();
      return   institutionUtils.institutionGlobalList.stream().filter(i->i.getBececode().equals(finalBeceCode)).findFirst().get().getSubjectList().stream().map(s->subjectUtil.mapSubject_ToSubjectResponse(s)).collect(Collectors.toList());

    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjectsByClassGroup(SingleStringRequest classGroup1) {
        String classGroup= classGroup1.getVal();
       return subjectUtil.subjectGlobalList.stream().filter(s->s.getClassGroup().equals(classGroup)).map(m->subjectUtil.mapSubject_ToSubjectResponse(m)).collect(Collectors.toList());
    }
}
