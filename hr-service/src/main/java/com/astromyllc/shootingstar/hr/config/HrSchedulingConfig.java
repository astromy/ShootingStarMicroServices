package com.astromyllc.shootingstar.hr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Switches on @Scheduled jobs for the HR service (the start-of-year holiday fetch). */
@Configuration
@EnableScheduling
public class HrSchedulingConfig {
}
