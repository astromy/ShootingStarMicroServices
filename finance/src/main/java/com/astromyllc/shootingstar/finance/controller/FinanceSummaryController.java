package com.astromyllc.shootingstar.finance.controller;

import com.astromyllc.shootingstar.finance.dto.request.SingleStringRequest;
import com.astromyllc.shootingstar.finance.dto.response.FinanceSummaryResponse;
import com.astromyllc.shootingstar.finance.service.FinanceSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Fee totals for the dashboards. The school code comes from the ORB proxy, which takes it from the signed-in user. */
@RestController
@RequestMapping("/api/finance")
public class FinanceSummaryController {

    private final FinanceSummaryService summaries;

    public FinanceSummaryController(FinanceSummaryService summaries) {
        this.summaries = summaries;
    }

    /** Body: {"val": "<school code>"} */
    @PostMapping("/dashboard-summary")
    public ResponseEntity<FinanceSummaryResponse> dashboardSummary(@RequestBody SingleStringRequest school) {
        return ResponseEntity.ok(summaries.summary(school.getVal()));
    }
}
