package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.paystack.PaystackPaymentResponse;
import com.astromyllc.astroorb.dto.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class FinanceController {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${gateway.host}")
    private String backendserve;

/*    @ResponseBody
    @RequestMapping(value = "create-bills", method = RequestMethod.POST)
    public ResponseEntity<String> addfinance(@RequestBody List<BillRequest> jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOSTLIST(Collections.singletonList(jso), backendserve + "/api/finance/create-bills");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "get-bills-by-institution", method = RequestMethod.POST)
    public ResponseEntity<String> getFinanceInstitutionByCode(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/finance/get-bills-by-institution");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "get-billing-by-institutionClass", method = RequestMethod.POST)
    public ResponseEntity<String> getClassBilling(@RequestBody BillingFetchRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/finance/getStudentBillsByInstitutionClass");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "bill-students-by-institution", method = RequestMethod.POST)
    public ResponseEntity<String> billStudents(@RequestBody BillingsRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/finance/create-billings");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "paystackWebhookResponse ", method = RequestMethod.POST)
    public ResponseEntity<String> receivePaystackWebhook(@RequestBody PaystackPaymentResponse webhookData) {
        log.info("Paystack Response -> {}", webhookData);
        ResponseEntity<String> response = BACKENDCOMMPOST(webhookData, backendserve + "/api/setup/paystackWebhookResponse");
        return response;
    }
*/

    // ── HELPER ───────────────────────────────────────────────────────────────
    private ResponseEntity<String> post(Object body, String url) {
        log.info("Finance proxy → {}", url);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(res.statusCode()).body(res.body());
        } catch (IOException | InterruptedException e) {
            log.error("Finance proxy error for {}: {}", url, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private ResponseEntity<String> get(String url) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET().build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(res.statusCode()).body(res.body());
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BILLS
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("create-bills")
    public ResponseEntity<String> createBills(@RequestBody List<BillRequest> body) {
        return post(body, backendserve + "/api/finance/create-bills");
    }

    @PostMapping("get-bills-by-institution")
    public ResponseEntity<String> getBillsByInstitution(@RequestBody SingleStringRequest body) {
        return post(body, backendserve + "/api/finance/get-bills-by-institution");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // BILLING (assign fees to students)
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("bill-students-by-institution")
    public ResponseEntity<String> billStudents(@RequestBody BillingsRequest body) {
        return post(body, backendserve + "/api/finance/bill-students-by-institution");
    }

    @PostMapping("get-billing-by-institutionClass")
    public ResponseEntity<String> getBillingByClass(@RequestBody BillingFetchRequest body) {
        return post(body, backendserve + "/api/finance/get-billing-by-institutionClass");
    }

    @PostMapping("getStudentBilling")
    public ResponseEntity<String> getStudentBilling(@RequestBody BillingFetchRequest body) {
        return post(body, backendserve + "/api/finance/getStudentBilling");
    }

    @PostMapping("getClassBilling")
    public ResponseEntity<String> getClassBilling(@RequestBody BillingFetchRequest body) {
        return post(body, backendserve + "/api/finance/getClassBilling");
    }

    @PostMapping("getSchoolBilling")
    public ResponseEntity<String> getSchoolBilling(@RequestBody BillingFetchRequest body) {
        return post(body, backendserve + "/api/finance/getSchoolBilling");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // STUDENT BILL (account summary per student)
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("getStudentBillByIdAndInstitution")
    public ResponseEntity<String> getStudentBill(@RequestBody StudentBillFetchRequest body) {
        return post(body, backendserve + "/api/finance/getStudentBillByIdAndInstitution");
    }

    @PostMapping("/api/mobile/getStudentBillDetails")
    public ResponseEntity<String> getStudentBillMobile(@RequestBody StudentBillFetchRequest body) {
        return post(body, backendserve + "/api/finance/getStudentBillByIdAndInstitution");
    }

    @PostMapping("getStudentBillsByInstitution")
    public ResponseEntity<String> getStudentBillsByInstitution(@RequestBody StudentBillFetchRequest body) {
        return post(body, backendserve + "/api/finance/getStudentBillsByInstitution");
    }

    @PostMapping("getStudentBillsByInstitutionClass")
    public ResponseEntity<String> getStudentBillsByClass(@RequestBody DynamicStringRequest body) {
        return post(body, backendserve + "/api/finance/getStudentBillsByInstitutionClass");
    }

    @PostMapping("getOwingStudents")
    public ResponseEntity<String> getOwingStudents(@RequestBody StudentBillFetchRequest body) {
        return post(body, backendserve + "/api/finance/getOwingStudents");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PAYMENTS (fee collection)
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("create-billPayment")
    public ResponseEntity<String> createPayment(@RequestBody Bill_PaymentRequest body) {
        return post(body, backendserve + "/api/finance/create-billPayment");
    }

    @PostMapping("api/mobile/verifyAndRecordPayment")
    public ResponseEntity<String> verifyAndRecordPayment(@RequestBody Bill_PaymentRequest body) {
        return post(body, backendserve + "/api/finance/create-billPayment");
    }

    @PostMapping("create-billPayments")
    public ResponseEntity<String> createPayments(@RequestBody List<Bill_PaymentRequest> body) {
        return post(body, backendserve + "/api/finance/create-billPayments");
    }

    @PostMapping("get-billPayments-by-institution")
    public ResponseEntity<String> getPaymentsByInstitution(@RequestBody BillFetchRequest body) {
        return post(body, backendserve + "/api/finance/get-billPayments-by-institution");
    }

    @PostMapping("get-billPayments-by-student")
    public ResponseEntity<String> getPaymentsByStudent(@RequestBody BillFetchRequest body) {
        return post(body, backendserve + "/api/finance/get-billPayments-by-student");
    }

    @PostMapping("get-billPayments-by-student-term")
    public ResponseEntity<String> getPaymentsByStudentTerm(@RequestBody Bill_PaymentRequest body) {
        return post(body, backendserve + "/api/finance/get-billPayments-by-student-term");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SALARY / PAYROLL
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("salary-settings/save")
    public ResponseEntity<String> saveSalarySettings(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/salary-settings/save");
    }

    @PostMapping("salary-settings/get")
    public ResponseEntity<String> getSalarySettings(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/salary-settings/get");
    }

    @PostMapping("salary/create")
    public ResponseEntity<String> createSalaryRun(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/salary/create");
    }

    @PostMapping("salary/create-batch")
    public ResponseEntity<String> createSalaryBatch(@RequestBody List<Map<String, Object>> body) {
        return post(body, backendserve + "/api/finance/salary/create-batch");
    }

    @PostMapping("salary/get-by-institution")
    public ResponseEntity<String> getSalariesByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/salary/get-by-institution");
    }

    @PostMapping("salary/payslip")
    public ResponseEntity<String> getPayslip(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/salary/payslip");
    }

    /**
     * Approve: POST /salary/approve/{id}?approvedBy=name
     * The JS calls fetchPost("salary/approve/123?approvedBy=Admin", {})
     * so we use a wildcard mapping.
     */
    @RequestMapping(value = "salary/approve/{salaryId}", method = RequestMethod.POST)
    public ResponseEntity<String> approveSalary(
            @PathVariable Long salaryId,
            @RequestParam String approvedBy,
            @RequestBody(required = false) String body) {
        return post(new Object(), backendserve + "/api/finance/salary/approve/" + salaryId + "?approvedBy=" + approvedBy);
    }

    @RequestMapping(value = "salary/mark-paid/{salaryId}", method = RequestMethod.POST)
    public ResponseEntity<String> markSalaryPaid(
            @PathVariable Long salaryId,
            @RequestParam String processedBy,
            @RequestParam(required = false) String externalReference,
            @RequestBody(required = false) String body) {
        String url = backendserve + "/api/finance/salary/mark-paid/" + salaryId
                + "?processedBy=" + processedBy
                + (externalReference != null ? "&externalReference=" + externalReference : "");
        return post(new Object(), url);
    }

    @GetMapping("salary/get/{salaryId}")
    public ResponseEntity<String> getSalaryById(@PathVariable Long salaryId) {
        return get(backendserve + "/api/finance/salary/get/" + salaryId);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PAYSTACK WEBHOOK
    // ══════════════════════════════════════════════════════════════════════════

    @PostMapping("paystackWebhookResponse")
    public ResponseEntity<String> paystackWebhook(@RequestBody PaystackPaymentResponse body) {
        log.info("Paystack webhook received");
        return post(body, backendserve + "/api/setup/paystackWebhookResponse");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CASH FLOW (derived — no separate backend endpoint needed)
    // Orb calls get-billPayments-by-institution + salary/get-by-institution
    // and computes cash flow client-side in _financeCashFlow.js
    // ══════════════════════════════════════════════════════════════════════════


    private ResponseEntity<String> BACKENDCOMMPOSTLIST(List<Object> jso, String url) {

        HttpURLConnection httpURLConnection = null;
        StringBuilder response = new StringBuilder();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        BufferedReader br = null;
        try {

            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            String json = ow.writeValueAsString(jso);
            json = json.substring(1, json.length() - 1);
            wr.write(json.getBytes(StandardCharsets.UTF_8));
            wr.flush();
            wr.close();

            InputStream inputStream;

            int status = httpURLConnection.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_ACCEPTED && status != HttpURLConnection.HTTP_CREATED && status != HttpURLConnection.HTTP_NO_CONTENT)
                inputStream = httpURLConnection.getErrorStream();
            else
                inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            try (BufferedReader brIn = new BufferedReader(
                    new InputStreamReader(inputStream, "utf-8"))) {
                String responseLine = null;
                while ((responseLine = brIn.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            // System.out.println(response.toString());
            return ResponseEntity.ok(response.toString());
        } catch (IOException e) {
            // Log the error for debugging
            e.printStackTrace();
            // Return a generic error response with status 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while fetching institution: " + e.getMessage());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ResponseEntity<String> BACKENDCOMMPOST(Object jso, String url) {

        log.info("Calling API: {}", url);
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(jso)))
                    .build();
            log.info("Calling API With REQUEST: {}", request);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (IOException | InterruptedException e) {
            log.error(String.valueOf(e));
        }
        return null;
    }

    // ══════════════════════════════════════════════════════════════════════════
// LEDGER BOOKS (Chart of Accounts)
//
// Used by:
//   _financeLedgers.js         → ledger/create, ledger/get-by-institution,
//                                 ledger/get-records
//   _financeTrialBalance.js    → ledger/trial-balance
//   _financeIncomeStatement.js → ledger/income-statement
// ══════════════════════════════════════════════════════════════════════════

    @PostMapping("ledger/create")
    public ResponseEntity<String> createLedgerAccount(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/ledger/create");
    }

    @PostMapping("ledger/get-by-institution")
    public ResponseEntity<String> getLedgersByInstitution(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/ledger/get-by-institution");
    }

    @PostMapping("ledger/get-records")
    public ResponseEntity<String> getLedgerRecords(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/ledger/get-records");
    }

    @PostMapping("ledger/trial-balance")
    public ResponseEntity<String> getTrialBalance(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/ledger/trial-balance");
    }

    @PostMapping("ledger/income-statement")
    public ResponseEntity<String> getIncomeStatement(@RequestBody Map<String, Object> body) {
        return post(body, backendserve + "/api/finance/ledger/income-statement");
    }

}
