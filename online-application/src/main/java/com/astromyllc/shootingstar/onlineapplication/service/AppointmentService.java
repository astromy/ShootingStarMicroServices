package com.astromyllc.shootingstar.onlineapplication.service;

import com.astromyllc.shootingstar.onlineapplication.serviceInterface.AppointmentServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentService  implements AppointmentServiceInterface {

}
