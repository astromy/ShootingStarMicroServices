package com.astromyllc.shootingstar.hr.controller;

import com.astromyllc.shootingstar.hr.dto.request.HolidayRequest;
import com.astromyllc.shootingstar.hr.dto.request.WorkHoursSettingsRequest;
import com.astromyllc.shootingstar.hr.dto.response.CountResponse;
import com.astromyllc.shootingstar.hr.dto.response.HolidayResponse;
import com.astromyllc.shootingstar.hr.dto.response.HolidaySeedResponse;
import com.astromyllc.shootingstar.hr.dto.response.WorkHoursSettingsResponse;
import com.astromyllc.shootingstar.hr.service.HolidayService;
import com.astromyllc.shootingstar.hr.service.NationalHolidayService;
import com.astromyllc.shootingstar.hr.service.WorkHoursService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** The school's holiday calendar and working week - the Setup side of leave, man-hours and overtime. */
@RestController
public class HolidayController {

    private final HolidayService holidayService;
    private final WorkHoursService workHoursService;
    private final NationalHolidayService nationalHolidayService;

    public HolidayController(HolidayService holidayService, WorkHoursService workHoursService,
                             NationalHolidayService nationalHolidayService) {
        this.holidayService = holidayService;
        this.workHoursService = workHoursService;
        this.nationalHolidayService = nationalHolidayService;
    }

    @PostMapping("/api/hr/getHolidays")
    public List<HolidayResponse> getHolidays(@RequestBody HolidayRequest request) {
        return holidayService.list(request.institutionCode(), request.year());
    }

    @PostMapping("/api/hr/seedGhanaHolidays")
    public HolidaySeedResponse seedGhanaHolidays(@RequestBody HolidayRequest request) {
        return holidayService.seed(request.institutionCode(), request.year());
    }

    @PostMapping("/api/hr/refreshHolidaysFromApi")
    public HolidaySeedResponse refreshHolidaysFromApi(@RequestBody HolidayRequest request) {
        return holidayService.refreshFromApi(request.institutionCode(), request.year());
    }

    /** Where this year's dates came from and when - shown above the holiday list. */
    @PostMapping("/api/hr/getHolidaySyncStatus")
    public NationalHolidayService.Status getHolidaySyncStatus(@RequestBody HolidayRequest request) {
        return nationalHolidayService.status(request.year() != null ? request.year() : java.time.Year.now().getValue());
    }

    @PostMapping("/api/hr/saveHoliday")
    public HolidayResponse saveHoliday(@RequestBody HolidayRequest request) {
        return holidayService.save(request);
    }

    @PostMapping("/api/hr/deleteHoliday")
    public CountResponse deleteHoliday(@RequestBody HolidayRequest request) {
        holidayService.delete(request.institutionCode(), request.id());
        return new CountResponse(1);
    }

    @PostMapping("/api/hr/getWorkHoursSettings")
    public WorkHoursSettingsResponse getWorkHoursSettings(@RequestBody WorkHoursSettingsRequest request) {
        return workHoursService.get(request.institutionCode());
    }

    @PostMapping("/api/hr/saveWorkHoursSettings")
    public WorkHoursSettingsResponse saveWorkHoursSettings(@RequestBody WorkHoursSettingsRequest request) {
        return workHoursService.save(request);
    }
}
