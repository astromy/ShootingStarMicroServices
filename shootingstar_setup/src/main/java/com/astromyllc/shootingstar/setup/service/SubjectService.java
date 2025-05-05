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

import java.util.*;
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
    public List<Optional<SubjectResponse>> createSubject(SubjectRequest subjectRequest) {
        log.info("Subject Request {}", subjectRequest);

        return InstitutionUtils.institutionGlobalList.stream()
                .filter(x -> x.getBececode().equalsIgnoreCase(subjectRequest.getInstitution())) // Filter by BECE code
                .findFirst() // Find the first matching institution
                .map(institution -> {
                    // Ensure the subject list is not null, and process it directly
                    institution.setSubjectList(Optional.ofNullable(institution.getSubjectList()).orElse(new ArrayList<>()));

                    // Convert existing subjects into a Set for quick lookup (name + classGroup as unique key)
                    Set<String> existingSubjects = institution.getSubjectList().stream()
                            .map(sub -> (sub.getName().toLowerCase() + "_" + sub.getClassGroup().toLowerCase())) // Unique key
                            .collect(Collectors.toSet());

                    // Filter out subjects that already exist and add new subjects
                    List<Subject> newSubjects=
                            subjectRequest.getSubjectDetails().stream()
                                    .map(s->{
                                       Subject ns= SubjectUtil.mapSubjectRequest_ToSubject(s);
                                       ns.setInstitution(institution);
                                       return ns;
                                    })
                                    .filter(sub -> !existingSubjects.contains(sub.getName().toLowerCase() + "_" + sub.getClassGroup().toLowerCase()))
                                    .collect(Collectors.toList());


                    // Save the updated institution with the new subject list
                    subjectRepository.saveAll(newSubjects);
                    institution.getSubjectList().addAll(newSubjects);

                    log.info("INSTITUTION ID {}", institution.getIdInstitution());

                    // Return the list of subject responses wrapped in Optional
                    return institution.getSubjectList().stream()
                            .map(subjectUtil::mapSubject_ToSubjectResponse)
                            .collect(Collectors.toList());
                })
                .map(ArrayList::new)  // Collect into a List<Optional<SubjectResponse>>
                .orElseGet(() -> {
                    log.warn("Institution not found!");
                    return new ArrayList<Optional<SubjectResponse>>();  // Return an empty list if institution is not found
                });

    }

    @Override
    public void createSubjects(List<SubjectRequest> subjectRequestList) {

    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjects() {
       return SubjectUtil.subjectGlobalList.stream().map(subjectUtil::mapSubject_ToSubjectResponse).toList();
    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjectsByClass(ClassesRequest classesRequest) {
       return InstitutionUtils.institutionGlobalList.stream().filter(i-> false).findFirst().get().getClassList()
                .stream().map(cl->classesRequest.getClassDetailList().stream().filter(c->c.getId().equals(cl.getIdClasses()))).map(s->subjectUtil.mapSubject_ToSubjectResponse((Subject) s)).toList();
    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjectsByInstitution(SingleStringRequest institutionRequest) {
        String finalBeceCode= institutionRequest.getVal();
        List<Optional<SubjectResponse>> returnVal=  Optional.ofNullable(InstitutionUtils.institutionGlobalList)  // Check if institutionGlobalList is null
                .map(list -> list.stream()  // Stream over the list if it's not null
                        .filter(i -> i.getBececode().equalsIgnoreCase(finalBeceCode))  // Filter based on BECE code
                        .findFirst()  // Find the first matching institution
                        .flatMap(i -> Optional.ofNullable(i.getSubjectList()))  // If institution found, get subject list (handle null)
                        .map(subjectList -> subjectList.stream()  // If subject list is not null, stream it
                                .map(s -> Optional.ofNullable(subjectUtil.mapSubject_ToSubjectResponse(s)))  // Map each subject safely
                                .filter(Optional::isPresent)  // Filter out null results
                                .map(Optional::get)  // Get the actual subject response
                                .toList())  // Collect to list
                        .orElse(Collections.emptyList()))  // If no matching institution or subject list is null, return an empty list
                .orElse(Collections.emptyList());
        log.debug("\n\n\n\n Subject List..... {}",returnVal );
        return returnVal;
    }

    @Override
    public Optional<List<Optional<SubjectResponse>>> getAllSubjectsByInstitutionAndClassGroup(SubjectDetails institutionRequest) {
        Optional<List<Optional<SubjectResponse>>> collect = Optional.of(InstitutionUtils.institutionGlobalList.stream()
                .filter(i -> i.getBececode().equalsIgnoreCase(institutionRequest.getName())) // Filter by BECE code
                .findFirst() // Find the first matching institution
                .map(institution -> institution.getSubjectList().stream() // Get the subject list
                        .filter(ps -> ps.getClassGroup().equalsIgnoreCase(institutionRequest.getClassGroup())) // Filter by class group
                        .map(subjectUtil::mapSubject_ToSubjectResponse) // Map to SubjectResponse
                        .collect(Collectors.toList()) // Collect the results into a List
                ).orElse(Collections.emptyList()));
        return collect;
    }

    @Override
    public List<Optional<SubjectResponse>> getAllSubjectsByClassGroup(SingleStringRequest classGroup1) {
        String classGroup= classGroup1.getVal();
       return SubjectUtil.subjectGlobalList.stream().filter(s->s.getClassGroup().equalsIgnoreCase(classGroup)).map(m->subjectUtil.mapSubject_ToSubjectResponse(m)).toList();
    }
}
