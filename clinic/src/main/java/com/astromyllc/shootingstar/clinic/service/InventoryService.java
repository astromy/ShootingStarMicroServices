package com.astromyllc.shootingstar.clinic.service;

import com.astromyllc.shootingstar.clinic.serviceInterface.InventoryServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService implements InventoryServiceInterface {
}
