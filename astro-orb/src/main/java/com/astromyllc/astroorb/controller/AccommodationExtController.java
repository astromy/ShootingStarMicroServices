package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.subscription.RequiresPlan;
import com.astromyllc.astroorb.subscription.SubscriptionPlan;

import com.astromyllc.astroorb.dto.request.AmenityRequest;
import com.astromyllc.astroorb.dto.request.ConsumableRequest;
import com.astromyllc.astroorb.dto.request.CreateDutyRotationRequest;
import com.astromyllc.astroorb.dto.request.DutyTypeRequest;
import com.astromyllc.astroorb.dto.request.SingleIdRequest;
import com.astromyllc.astroorb.dto.request.SingleStringRequest;
import com.astromyllc.astroorb.dto.request.TicketRequest;
import com.astromyllc.astroorb.dto.request.UpdateDutyStatusRequest;
import com.astromyllc.astroorb.dto.request.UpdateTicketStatusRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// Mirrors AccommodationController's pattern and proxies to the same set of
// new backend endpoints added in AccommodationExtController on the backend
// side. Separate file so it drops in without touching the existing
// AccommodationController.
@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
@RequiresPlan(SubscriptionPlan.GROWTH)
public class AccommodationExtController {

    @Value("${gateway.host}")
    private String backendserve;

    // ==================== Duty Types (catalog) ====================

    @ResponseBody
    @RequestMapping(value = "addDutyType", method = RequestMethod.POST)
    public ResponseEntity<String> addDutyType(@RequestBody DutyTypeRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/addDutyType";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "getDutyTypes", method = RequestMethod.POST)
    public ResponseEntity<String> getDutyTypes(@RequestBody SingleStringRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/getDutyTypes";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "deleteDutyType", method = RequestMethod.POST)
    public ResponseEntity<String> deleteDutyType(@RequestBody SingleIdRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/deleteDutyType";
        return BACKENDCOMMPOST(jso, url);
    }

    // ==================== Duty Roster (rotations) ====================

    @ResponseBody
    @RequestMapping(value = "createDutyRotation", method = RequestMethod.POST)
    public ResponseEntity<String> createDutyRotation(@RequestBody CreateDutyRotationRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/createDutyRotation";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "updateDutyStatus", method = RequestMethod.POST)
    public ResponseEntity<String> updateDutyStatus(@RequestBody UpdateDutyStatusRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/updateDutyStatus";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "getBlockDutyRoster", method = RequestMethod.POST)
    public ResponseEntity<String> getBlockDutyRoster(@RequestBody SingleIdRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/getBlockDutyRoster";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "cancelDutyRotation", method = RequestMethod.POST)
    public ResponseEntity<String> cancelDutyRotation(@RequestBody SingleStringRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/cancelDutyRotation";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "deleteDutyAssignment", method = RequestMethod.POST)
    public ResponseEntity<String> deleteDutyAssignment(@RequestBody SingleIdRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/deleteDutyAssignment";
        return BACKENDCOMMPOST(jso, url);
    }

    // ==================== Amenities ====================

    @ResponseBody
    @RequestMapping(value = "addAmenity", method = RequestMethod.POST)
    public ResponseEntity<String> addAmenity(@RequestBody AmenityRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/addAmenity";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "updateAmenity", method = RequestMethod.POST)
    public ResponseEntity<String> updateAmenity(@RequestBody AmenityRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/updateAmenity";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "getBlockAmenities", method = RequestMethod.POST)
    public ResponseEntity<String> getBlockAmenities(@RequestBody SingleIdRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/getBlockAmenities";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "deleteAmenity", method = RequestMethod.POST)
    public ResponseEntity<String> deleteAmenity(@RequestBody SingleIdRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/deleteAmenity";
        return BACKENDCOMMPOST(jso, url);
    }

    // ==================== Consumables ====================

    @ResponseBody
    @RequestMapping(value = "addConsumable", method = RequestMethod.POST)
    public ResponseEntity<String> addConsumable(@RequestBody ConsumableRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/addConsumable";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "updateConsumable", method = RequestMethod.POST)
    public ResponseEntity<String> updateConsumable(@RequestBody ConsumableRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/updateConsumable";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "getBlockConsumables", method = RequestMethod.POST)
    public ResponseEntity<String> getBlockConsumables(@RequestBody SingleIdRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/getBlockConsumables";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "deleteConsumable", method = RequestMethod.POST)
    public ResponseEntity<String> deleteConsumable(@RequestBody SingleIdRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/deleteConsumable";
        return BACKENDCOMMPOST(jso, url);
    }

    // ==================== Faults & Complaints (tickets) ====================

    @ResponseBody
    @RequestMapping(value = "raiseTicket", method = RequestMethod.POST)
    public ResponseEntity<String> raiseTicket(@RequestBody TicketRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/raiseTicket";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "updateTicketStatus", method = RequestMethod.POST)
    public ResponseEntity<String> updateTicketStatus(@RequestBody UpdateTicketStatusRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/updateTicketStatus";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "getBlockTickets", method = RequestMethod.POST)
    public ResponseEntity<String> getBlockTickets(@RequestBody SingleIdRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/getBlockTickets";
        return BACKENDCOMMPOST(jso, url);
    }

    @ResponseBody
    @RequestMapping(value = "getInstitutionTickets", method = RequestMethod.POST)
    public ResponseEntity<String> getInstitutionTickets(@RequestBody SingleStringRequest jso) throws IOException {
        String url = backendserve + "/api/accommodation/getInstitutionTickets";
        return BACKENDCOMMPOST(jso, url);
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
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (IOException | InterruptedException e) {
            log.error(String.valueOf(e));
        }
        return null;
    }
}
