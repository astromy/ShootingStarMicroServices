package com.astromyllc.shootingstar.finance.serviceInterface;

import com.astromyllc.shootingstar.finance.dto.request.PayrollActionRequest;
import com.astromyllc.shootingstar.finance.dto.request.RunPayrollRequest;
import com.astromyllc.shootingstar.finance.dto.request.SalaryProfileRequest;
import com.astromyllc.shootingstar.finance.dto.response.PayrollActionResponse;
import com.astromyllc.shootingstar.finance.dto.response.SalaryProfileResponse;

import java.util.List;

/**
 * Salary profiles, monthly payroll runs and bulk approval / payment.
 *
 * Every call carries the institutionCode set by the gateway from the login,
 * and only touches that school's records. Invalid input throws
 * IllegalArgumentException with a message meant for the user.
 */
public interface PayrollServiceInterface {

    List<SalaryProfileResponse> getProfiles(String institutionCode);

    SalaryProfileResponse saveProfile(SalaryProfileRequest request);

    /** Creates a PENDING run for every active profile whose staff member is in HR, skipping anyone already run for the period. */
    PayrollActionResponse runPayroll(RunPayrollRequest request);

    /** PENDING -> APPROVED. */
    PayrollActionResponse approve(PayrollActionRequest request);

    /** APPROVED -> PAID. */
    PayrollActionResponse markPaid(PayrollActionRequest request);

    /** Removes PENDING runs (e.g. to fix a profile and run again). */
    PayrollActionResponse deletePending(PayrollActionRequest request);
}
