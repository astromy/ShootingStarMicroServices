package com.astromyllc.shootingstar.onlineapplication.repository;

import com.astromyllc.shootingstar.onlineapplication.model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppointmentRepository  extends MongoRepository<Appointment, String> {

}
