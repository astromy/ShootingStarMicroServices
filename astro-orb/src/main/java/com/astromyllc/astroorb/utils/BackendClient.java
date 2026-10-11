package com.astromyllc.astroorb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * One shared connection to the API gateway for the screens that read lists, counts and pictures.
 *
 * It is created once and re-used, so requests share open connections instead of paying for a new
 * HTTP client (and its threads) on every call.
 */
@Component
public class BackendClient {

    /** What came back from a POST. */
    public record Reply(int status, String body) {
    }

    /** What came back from a picture request. */
    public record Image(int status, String contentType, String eTag, String cacheControl, byte[] bytes) {
    }

    /** The service could not be reached at all (as opposed to answering with an error). */
    public static class Unavailable extends Exception {
        public Unavailable(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private final String gateway;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    public BackendClient(@Value("${gateway.host}") String gateway) {
        this.gateway = gateway;
    }

    /** POSTs the body as JSON. */
    public Reply post(String path, Object body, Duration timeout) throws Unavailable {
        String url = gateway + path;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(timeout)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return new Reply(response.statusCode(), response.body());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Could not write the request for " + path, e);
        } catch (IOException e) {
            throw new Unavailable("Could not reach " + path + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Unavailable("Interrupted while calling " + path, e);
        }
    }

    /** GETs a picture, passing the browser's If-None-Match along so unchanged pictures come back as 304. */
    public Image getImage(String path, String ifNoneMatch, Duration timeout) throws Unavailable {
        String url = gateway + path;
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(timeout).GET();
            if (ifNoneMatch != null && !ifNoneMatch.isBlank()) builder.header("If-None-Match", ifNoneMatch);
            HttpResponse<byte[]> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
            return new Image(response.statusCode(),
                    response.headers().firstValue("Content-Type").orElse(null),
                    response.headers().firstValue("ETag").orElse(null),
                    response.headers().firstValue("Cache-Control").orElse(null),
                    response.body());
        } catch (IOException e) {
            throw new Unavailable("Could not reach " + path + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Unavailable("Interrupted while calling " + path, e);
        }
    }
}
