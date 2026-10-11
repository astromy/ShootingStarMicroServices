package com.astromyllc.shootingstar.hr.model;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "national_holiday")
public class NationalHoliday {
    @Id
    private String id;
    @Indexed
    private int year;
    private String name;
    // ISO date the law names
    private String date;
    // ISO date staff are off (differs when the provider lists a substitute day)
    private String observedDate;
    // which service it came from: MANSA | HOLIDAYSMAP
    private String provider;
    private Instant fetchedAt;

    public NationalHoliday() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getObservedDate() {
        return observedDate;
    }

    public void setObservedDate(String observedDate) {
        this.observedDate = observedDate;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Instant getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Instant fetchedAt) {
        this.fetchedAt = fetchedAt;
    }
}
