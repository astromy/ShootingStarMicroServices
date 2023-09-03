package com.astromyllc.shootingstar.academics.service;

import com.astromyllc.shootingstar.academics.serviceInterface.ExamsAnswersServiceInterface;
import com.astromyllc.shootingstar.academics.serviceInterface.ExamsAssessmentServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamsAnswersService implements ExamsAnswersServiceInterface {

}
