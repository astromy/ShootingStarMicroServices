package com.astromyllc.astroorb.controller;

import com.astromyllc.astroorb.dto.request.*;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@ResponseBody
@RequiredArgsConstructor
public class HRController {

    @Value("${gateway.host}")
    private String backendserve;

    @ResponseBody
    @RequestMapping(value = "create-staff", method = RequestMethod.POST)
    public ResponseEntity<String> addfinance(@RequestBody List<StaffRequest> jso) throws IOException {
        String url = backendserve + "/api/hr/createStaff";
        ResponseEntity<String> response = BACKENDCOMMPOSTLIST(jso, url);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "addStaffPermissions", method = RequestMethod.POST)
    public ResponseEntity<String> addStaffPermissions(@RequestBody List<StaffPermissionsRequest> jso) throws IOException {
        String url = backendserve + "/api/hr/addStaffPermissions";
        ResponseEntity<String> response = BACKENDCOMMPOSTLIST(jso, url);
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "get-staff-by-institution", method = RequestMethod.POST)
    public ResponseEntity<String> getFinanceInstitutionByCode(@RequestBody SingleStringRequest jso) throws IOException {

        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/hr/getStaffByCode");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "get-staff-by-institution-and-code", method = RequestMethod.POST)
    public ResponseEntity<String> getClassBilling(@RequestBody BillingFetchRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/hr/get-billings-by-institution");
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "getStaffByStaffId", method = RequestMethod.POST)
    public ResponseEntity<String> getStaffByStaffId(@RequestBody SingleStringRequest jso) {
        ResponseEntity<String> response = BACKENDCOMMPOST(jso, backendserve + "/api/hr/getStaffByStaffId");
        return response;
    }

    // Mobile calls this once it has an Expo push token (e.g. after login,
    // or whenever the token refreshes) so admin staff can receive transport
    // compliance alerts and similar notifications.
    @ResponseBody
    @RequestMapping(value = "api/mobile/registerPushToken", method = RequestMethod.POST)
    public ResponseEntity<String> registerPushToken(@RequestBody RegisterPushTokenRequest jso) throws IOException, InterruptedException {
        log.info("REQUEST registerPushToken OF..... {}", jso);
        return BACKENDCOMMPOST(jso, backendserve + "/api/hr/registerPushToken");
    }

    // NOTE: "/api/hr/staffClockIn" on the HR microservice is an assumed
    // endpoint name/payload — it doesn't exist in any HR-service code shared
    // so far. Confirm the real contract there and adjust the forwarded body
    // below if needed. The geofence check itself is real and self-contained:
    // it calls the setup service directly, so it works regardless of what
    // the HR side ultimately expects.
    @ResponseBody
    @RequestMapping(value = "api/mobile/staffClockIn", method = RequestMethod.POST)
    public ResponseEntity<String> staffClockIn(@RequestBody StaffClockInRequest jso) throws IOException, InterruptedException {
        log.info("REQUEST staffClockIn OF..... {}", jso);

        List<GeoPoint> boundary = fetchGeofenceBoundary(jso.getInstitutionCode());
        if (boundary.size() < 3) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\":\"No school boundary has been configured yet. Ask an admin to set it up under School Boundary.\"}");
        }

        if (!isPointInPolygon(jso.getLatitude(), jso.getLongitude(), boundary)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\":\"You appear to be outside the school boundary. Move closer to campus and try again.\"}");
        }

        // Inside the boundary — record the clock event on the HR side.
        return BACKENDCOMMPOST(jso, backendserve + "/api/hr/staffClockIn");
    }

    // Fetches the institution's saved boundary from the setup service and
    // parses it into plain points. Returns an empty list if nothing has
    // been saved yet (setup service returns Optional.empty() -> null body,
    // or a GeofenceBoundaryResponse with an empty "boundary" array).
    private List<GeoPoint> fetchGeofenceBoundary(String institutionCode) throws IOException, InterruptedException {
        ResponseEntity<String> response = BACKENDCOMMPOST(
                SingleStringRequest.builder().val(institutionCode).build(),
                backendserve + "/api/setup/getGeofenceBoundary");

        List<GeoPoint> points = new ArrayList<>();
        if (response == null || response.getBody() == null || response.getBody().isBlank()) {
            return points;
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        JsonNode boundaryNode = root.has("boundary") ? root.get("boundary") : root;
        if (boundaryNode == null || !boundaryNode.isArray()) {
            return points;
        }

        for (JsonNode p : boundaryNode) {
            points.add(new GeoPoint(p.get("latitude").asDouble(), p.get("longitude").asDouble()));
        }
        return points;
    }

    // Ray-casting point-in-polygon test — same algorithm as the mobile
    // client's utils/geo.js isPointInPolygon, ported so the check is
    // authoritative server-side rather than trusting the client's own
    // "am I inside" claim. Polygon is treated as closed automatically.
    private boolean isPointInPolygon(double lat, double lon, List<GeoPoint> polygon) {
        boolean inside = false;
        int n = polygon.size();
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon.get(i).getLongitude(), yi = polygon.get(i).getLatitude();
            double xj = polygon.get(j).getLongitude(), yj = polygon.get(j).getLatitude();

            boolean intersects = ((yi > lat) != (yj > lat))
                    && (lon < (xj - xi) * (lat - yi) / (yj - yi) + xi);
            if (intersects) {
                inside = !inside;
            }
        }
        return inside;
    }


    private ResponseEntity<String> BACKENDCOMMPOSTLIST(Object jso, String url) {
        try {
            // Strip the outer array wrapper the same way the old code did

            String json = new ObjectMapper().writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(jso);

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            json = ow.writeValueAsString(jso);

            log.info("Calling API: {}", url);

            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(300))  // large payload needs more time
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode()).body(response.body());

        } catch (IOException | InterruptedException e) {
            log.error("Error calling backend: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calling backend: " + e.getMessage());
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