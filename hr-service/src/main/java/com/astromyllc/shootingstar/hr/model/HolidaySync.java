package com.astromyllc.shootingstar.hr.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "holiday_sync")
public class HolidaySync {
    @Id
    private String id;
    // one record per year: the last attempt to fetch it
    @Indexed
    private int year;
    private boolean ok;
    private Instant fetchedAt;
    // holidays kept from the last good fetch
    private int count;
    // service the last good fetch came from: MANSA | HOLIDAYSMAP
    private String provider;
    // YEAR/RANGE (one request) | DAILY (one request per day)
    private String mode;
    // HolidaysMap requests used by the last good fetch
    private int requests;
    // why the last attempt failed, if it did
    private String message;

    public HolidaySync() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
