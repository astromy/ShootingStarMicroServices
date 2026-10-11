package com.astromyllc.shootingstar.finance.utils;

import com.astromyllc.shootingstar.finance.model.SalaryItem;
import com.astromyllc.shootingstar.finance.model.SalarySettings;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Works out one staff member's pay for one run, the Ghana way:
 *
 *   gross pay       = basic + allowances
 *   employee SSNIT  = employee rate x basic        (deducted)
 *   employer SSNIT  = employer rate x basic        (the school's cost - shown, not deducted)
 *   taxable income  = gross pay - employee SSNIT
 *   PAYE            = the school's monthly tax bands applied to taxable income
 *   net pay         = gross pay - (profile deductions + employee SSNIT + PAYE)
 *
 * Tax bands are stored on SalarySettings as JSON, monthly amounts in cedis:
 *   [{"min":0,"max":490,"rate":0},{"min":490,"max":600,"rate":5}, ... ,{"min":50416.67,"max":null,"rate":35}]
 * The last band has "max": null (no upper limit).
 */
public final class PayrollCalculator {

    public static final String CODE_SSNIT_EMPLOYEE = "SSNIT_EMPLOYEE";
    public static final String CODE_PAYE = "PAYE";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private PayrollCalculator() {
    }

    public record TaxBand(double min, Double max, double rate) {
    }

    public record Result(
            List<SalaryItem> items,
            double basic,
            double totalAllowances,
            double grossPay,
            double employeeSsnit,
            double employerSsnit,
            double taxableIncome,
            double incomeTax,
            double totalDeductions,
            double netPay) {
    }

    /**
     * Parses and checks tax bands. Throws IllegalArgumentException with a
     * message for the admin if they're malformed, so a typo can't silently
     * produce wrong tax on every payslip.
     */
    public static List<TaxBand> parseBands(String json) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("Add the income tax bands in Salary Settings.");
        }
        JsonNode root;
        try {
            root = MAPPER.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("The income tax bands couldn't be read. Re-enter them in Salary Settings.");
        }
        if (root == null || !root.isArray() || root.isEmpty()) {
            throw new IllegalArgumentException("Add at least one income tax band in Salary Settings.");
        }

        List<TaxBand> bands = new ArrayList<>();
        for (JsonNode n : root) {
            double min = n.path("min").asDouble(-1);
            Double max = n.hasNonNull("max") ? n.get("max").asDouble() : null;
            double rate = n.path("rate").asDouble(-1);
            if (min < 0 || rate < 0 || rate > 100 || (max != null && max <= min)) {
                throw new IllegalArgumentException("Each tax band needs a starting amount, an ending amount above it, and a rate from 0 to 100%.");
            }
            bands.add(new TaxBand(min, max, rate));
        }

        if (bands.get(0).min() != 0) {
            throw new IllegalArgumentException("The first tax band must start at 0.");
        }
        for (int i = 1; i < bands.size(); i++) {
            TaxBand previous = bands.get(i - 1);
            if (previous.max() == null) {
                throw new IllegalArgumentException("Only the last tax band can have no upper limit.");
            }
            if (Math.abs(bands.get(i).min() - previous.max()) > 0.01) {
                throw new IllegalArgumentException("Each tax band must start where the one before it ends (gap or overlap at "
                        + previous.max() + ").");
            }
        }
        if (bands.get(bands.size() - 1).max() != null) {
            throw new IllegalArgumentException("The last tax band must have no upper limit, so every income is taxed.");
        }
        return bands;
    }

    public static double incomeTax(double taxableIncome, List<TaxBand> bands) {
        double tax = 0;
        for (TaxBand band : bands) {
            if (taxableIncome <= band.min()) break;
            double top = band.max() == null ? taxableIncome : Math.min(taxableIncome, band.max());
            tax += (top - band.min()) * band.rate() / 100.0;
        }
        return round(tax);
    }

    /**
     * @param profileItems the staff member's regular lines (not modified)
     */
    public static Result calculate(double basic, List<SalaryItem> profileItems, SalarySettings settings) {
        List<TaxBand> bands = parseBands(settings.getIncomeTaxBands());
        double employeeRate = settings.getSsnitEmployeeRate() == null ? 0 : settings.getSsnitEmployeeRate();
        double employerRate = settings.getSsnitEmployerRate() == null ? 0 : settings.getSsnitEmployerRate();

        List<SalaryItem> items = new ArrayList<>();
        double allowances = 0;
        double otherDeductions = 0;
        for (SalaryItem source : profileItems == null ? List.<SalaryItem>of() : profileItems) {
            double amount = Boolean.TRUE.equals(source.getIsPercentage())
                    ? round(basic * (source.getPercentageRate() == null ? 0 : source.getPercentageRate()) / 100.0)
                    : round(source.getAmount() == null ? 0 : source.getAmount());
            // The run keeps the cedi amount even for % lines, so payslips and
            // reports don't have to recalculate it.
            items.add(SalaryItem.builder()
                    .itemName(source.getItemName())
                    .itemType(source.getItemType())
                    .amount(amount)
                    .isPercentage(source.getIsPercentage())
                    .percentageRate(source.getPercentageRate())
                    // Normally null for profile lines; overtime lines carry OVERTIME_* so they can be told apart.
                    .itemCode(source.getItemCode())
                    .build());
            if ("DEDUCTION".equalsIgnoreCase(source.getItemType())) {
                otherDeductions += amount;
            } else {
                allowances += amount;
            }
        }

        double gross = round(basic + allowances);
        double employeeSsnit = round(basic * employeeRate / 100.0);
        double employerSsnit = round(basic * employerRate / 100.0);
        double taxable = round(Math.max(0, gross - employeeSsnit));
        double tax = incomeTax(taxable, bands);

        items.add(SalaryItem.builder()
                .itemName("SSNIT (" + trimRate(employeeRate) + "%)")
                .itemType("DEDUCTION")
                .amount(employeeSsnit)
                .isPercentage(true)
                .percentageRate(employeeRate)
                .itemCode(CODE_SSNIT_EMPLOYEE)
                .build());
        items.add(SalaryItem.builder()
                .itemName("Income Tax (PAYE)")
                .itemType("DEDUCTION")
                .amount(tax)
                .isPercentage(false)
                .itemCode(CODE_PAYE)
                .build());

        double totalDeductions = round(otherDeductions + employeeSsnit + tax);
        return new Result(items, basic, round(allowances), gross, employeeSsnit, employerSsnit,
                taxable, tax, totalDeductions, round(gross - totalDeductions));
    }

    private static String trimRate(double rate) {
        return rate == Math.rint(rate) ? String.valueOf((long) rate) : String.valueOf(rate);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
