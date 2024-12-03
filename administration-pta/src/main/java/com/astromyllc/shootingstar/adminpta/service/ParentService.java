package com.astromyllc.shootingstar.adminpta.service;

import com.astromyllc.shootingstar.adminpta.serviceInterface.ParentServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParentService implements ParentServiceInterface {
}
