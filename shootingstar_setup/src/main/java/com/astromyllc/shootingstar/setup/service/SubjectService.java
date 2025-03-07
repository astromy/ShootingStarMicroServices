package com.astromyllc.shootingstar.setup.service;

import com.astromyllc.shootingstar.setup.dto.request.*;
import com.astromyllc.shootingstar.setup.dto.response.SubjectResponse;
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
    public Optional<List<Optional<SubjectResponse>>> createSubject(SubjectRequest subjectRequest) {
        Optional<Institution> inst= InstitutionUtils.institutionGlobalList.stream().filter(x->x.getBececode().equalsIgnoreCase(subjectRequest.getInstitution())).findFirst();
        List<Subject> sj=inst.get().getSubjectList();
        sj.addAll(subjectRequest.getSubjectDetails().stream().map(SubjectUtil::mapSubjectRequest_ToSubject).toList());
        inst.get().setSubjectList(sj);
        institutionRepository.save(inst.get());
        Optional<List<Optional<SubjectResponse>>> sr= Optional.ofNullable(inst.get().getSubjectList().stream().map(s->subjectUtil.mapSubject_ToSubjectResponse(s)).collect(Collectors.toList()));
        return sr;

    }

    @Override
    public void createSubjects(List<SubjectRequest> subjectRequestList) {

    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjects() {
       return SubjectUtil.subjectGlobalList.stream().map(s->subjectUtil.mapSubject_ToSubjectResponse(s)).collect(Collectors.toList());
    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjectsByClass(ClassesRequest classesRequest) {
       return InstitutionUtils.institutionGlobalList.stream().filter(i->i.getIdInstitution().equals(classesRequest.getInstitution())).findFirst().get().getClassList()
                .stream().map(cl->classesRequest.getClassDetailList().stream().filter(c->c.getId().equals(cl.getIdClasses()))).map(s->subjectUtil.mapSubject_ToSubjectResponse((Subject) s)).collect(Collectors.toList());
    }

    @Override
    public Optional<List<Optional<SubjectResponse>>> getAllSubjectsByInstitution(SingleStringRequest institutionRequest) {
        String finalBeceCode= institutionRequest.getVal();
        Optional<List<Optional<SubjectResponse>>> collect = Optional.of(InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(finalBeceCode))
                .findFirst().get().getSubjectList().stream()
                .map(s->subjectUtil.mapSubject_ToSubjectResponse(s)).collect(Collectors.toList()));
        return collect;
    }

    @Override
    public Optional<List<Optional<SubjectResponse>>> getAllSubjectsByInstitutionAndClassGroup(SubjectDetails institutionRequest) {
        Optional<List<Optional<SubjectResponse>>> collect = Optional.of(InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(institutionRequest.getName()))
                .findFirst().get().getSubjectList().stream()
                        .filter(ps->ps.getClassGroup().equalsIgnoreCase(institutionRequest.getClassGroup()))
                .map(s->subjectUtil.mapSubject_ToSubjectResponse(s)).collect(Collectors.toList()));
        return collect;
    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjectsByClassGroup(SingleStringRequest classGroup1) {
        String classGroup= classGroup1.getVal();
       return SubjectUtil.subjectGlobalList.stream().filter(s->s.getClassGroup().equalsIgnoreCase(classGroup)).map(m->subjectUtil.mapSubject_ToSubjectResponse(m)).collect(Collectors.toList());
    }
}
