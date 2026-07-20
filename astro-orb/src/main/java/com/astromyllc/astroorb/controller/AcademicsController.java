package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class AcademicsController {

    @Value("${gateway.host}")
    private String backendserve;

    /*@Autowired
    private OAuth2AuthorizedClientService authorizedClientService;*/

    @ResponseBody
    @RequestMapping(value = "uploadAssignmentQuestions", method = RequestMethod.POST)
    public ResponseEntity<String> uploadAssignmentQuestions(@RequestBody List<AssignmentQuestionsRequest> jso) throws IOException {
        String url = backendserve + "/api/academics/submitAssignmentQuestions";
        ResponseEntity<String> response = BACKENDCOMMPOSTLIST(Collections.singletonList(jso), url);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "uploadExamsQuestions", method = RequestMethod.POST)
    public ResponseEntity<String> uploadExamsQuestions(@RequestBody List<AssignmentQuestionsRequest> jso) throws IOException {
        String url = backendserve + "/api/academics/submitExamsQuestions";
        ResponseEntity<String> response = BACKENDCOMMPOSTLIST(Collections.singletonList(jso), url);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "uploadAssesmentScores", method = RequestMethod.POST)
    public ResponseEntity<String> uploadAssesmentScores(@RequestBody List<ContinuousAssessmentRequest> jso) throws IOException {
        String url = backendserve + "/api/academics/submitContinuousAssessmentLList";
        ResponseEntity<String> response = BACKENDCOMMPOSTLIST(Collections.singletonList(jso), url);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "uploadExamsScores", method = RequestMethod.POST)
    public ResponseEntity<String> uploadExamsScores(@RequestBody List<ExamsAssessmentRequest> jso) throws IOException {
        String url = backendserve + "/api/academics/SubmitExamsAssessmentList";
        ResponseEntity<String> response = BACKENDCOMMPOSTLIST(Collections.singletonList(jso), url);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "generateStudentTerminalReport", method = RequestMethod.POST)
    public ResponseEntity<String> generateStudentTerminalReport(@RequestBody AcademicReportRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/generateTerminalReports");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "generateBroadsheet", method = RequestMethod.POST)
    public ResponseEntity<String> generateBroadsheet(@RequestBody AcademicReportRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/generateBroadsheet");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "generateUnconvertedBroadsheet", method = RequestMethod.POST)
    public ResponseEntity<String> generateUnconvertedBroadsheet(@RequestBody AcademicReportRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/generateUnconvertedBroadsheet");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "getExistingClassSubjectScores", method = RequestMethod.POST)
    public ResponseEntity<String> getExistingClassSubjectScores(@RequestBody AcademicReportRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/getExistingClassSubjectScores");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "fetchStudentTerminalReport", method = RequestMethod.POST)
    public ResponseEntity<String> fetchStudentTerminalReport(@RequestBody AcademicReportRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchStudentTerminalReport");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "postStudentReports", method = RequestMethod.POST)
    public ResponseEntity<String> postStudentReports(@RequestBody AcademicReportRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/postStudentTerminalReport");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "generateStudentTranscript", method = RequestMethod.POST)
    public ResponseEntity<String> generateStudentTranscript(@RequestBody SingleStringRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchStudentTranscript");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "generateClassTimetable", method = RequestMethod.POST)
    public ResponseEntity<String> generateClassTimetable(@RequestBody BillingFetchRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/get-billings-by-institution");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "api/mobile/resultsData", method = RequestMethod.POST)
    public ResponseEntity<String> getStudentResults(@RequestBody SingleStringRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/getStudentAcademicYearReport");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "timetable", method = RequestMethod.POST)
    public ResponseEntity<String> generateTimetable(@RequestBody DynamicStringRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/academics/timetable/generate");
        return response;
    }


    //================================================= QUESTION BANK =====================================================

    @ResponseBody
    @RequestMapping(value = "submitExamsQuestion", method = RequestMethod.POST)
    public ResponseEntity<String> submitExamsQuestion(@RequestBody ExamsQuestionsRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/submitExamsQuestion");
    }

    @ResponseBody
    @RequestMapping(value = "updateExamsQuestion", method = RequestMethod.POST)
    public ResponseEntity<String> updateExamsQuestion(@RequestBody ExamsQuestionsRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/updateExamsQuestion");
    }

    @ResponseBody
    @RequestMapping(value = "deleteExamsQuestion", method = RequestMethod.POST)
    public ResponseEntity<String> deleteExamsQuestion(@RequestBody ExamsQuestionsRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/deleteExamsQuestion");
    }

    @ResponseBody
    @RequestMapping(value = "fetchQuestionsByInstAndClassAndSubjAndTerm", method = RequestMethod.POST)
    public ResponseEntity<String> fetchQuestionsByInstAndClassAndSubjAndTerm(@RequestBody ExamsQuestionsRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchQuestionsByInstAndClassAndSubjAndTerm");
    }

    @ResponseBody
    @RequestMapping(value = "fetchQuestionsByInstAndClassAndSubj", method = RequestMethod.POST)
    public ResponseEntity<String> fetchQuestionsByInstAndClassAndSubj(@RequestBody ExamsQuestionsRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchQuestionsByInstAndClassAndSubj");
    }

    @ResponseBody
    @RequestMapping(value = "fetchQuestionsByInstAndClass", method = RequestMethod.POST)
    public ResponseEntity<String> fetchQuestionsByInstAndClass(@RequestBody ExamsQuestionsRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchQuestionsByInstAndClass");
    }


    //================================================= ASSIGNMENT GENERATION =====================================================

    @ResponseBody
    @RequestMapping(value = "createAssignment", method = RequestMethod.POST)
    public ResponseEntity<String> createAssignment(@RequestBody AssignmentRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/createAssignment");
    }

    @ResponseBody
    @RequestMapping(value = "updateAssignment", method = RequestMethod.POST)
    public ResponseEntity<String> updateAssignment(@RequestBody AssignmentRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/updateAssignment");
    }

    @ResponseBody
    @RequestMapping(value = "deleteAssignment", method = RequestMethod.POST)
    public ResponseEntity<String> deleteAssignment(@RequestBody AssignmentRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/deleteAssignment");
    }

    @ResponseBody
    @RequestMapping(value = "fetchAssignmentsByInstitution", method = RequestMethod.POST)
    public ResponseEntity<String> fetchAssignmentsByInstitution(@RequestBody AssignmentRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchAssignmentsByInstitution");
    }

    @ResponseBody
    @RequestMapping(value = "fetchAssignmentsByClass", method = RequestMethod.POST)
    public ResponseEntity<String> fetchAssignmentsByClass(@RequestBody AssignmentRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchAssignmentsByClass");
    }

    @ResponseBody
    @RequestMapping(value = "fetchAssignmentsByStaff", method = RequestMethod.POST)
    public ResponseEntity<String> fetchAssignmentsByStaff(@RequestBody AssignmentRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchAssignmentsByStaff");
    }

    @ResponseBody
    @RequestMapping(value = "fetchAssignmentsByClassAndSubject", method = RequestMethod.POST)
    public ResponseEntity<String> fetchAssignmentsByClassAndSubject(@RequestBody AssignmentRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchAssignmentsByClassAndSubject");
    }

    @ResponseBody
    @RequestMapping(value = "fetchAssignmentQuestionsByClassAndSubjectAndTerm", method = RequestMethod.POST)
    public ResponseEntity<String> fetchAssignmentQuestionsByClassAndSubjectAndTerm(@RequestBody AssignmentQuestionsRequest jso) {
        return BACKENDCOMMPOST(jso, backendserve + "/api/academics/fetchAssignmentQuestionsByClassAndSubjectAndTerm");
    }


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
                    //log.info("Response: {}", responseLine);
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

}
