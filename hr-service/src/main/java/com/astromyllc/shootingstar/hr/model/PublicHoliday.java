package com.astromyllc.shootingstar.hr.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "public_holiday")
public class PublicHoliday {
    @Id
    private String id;
    @Indexed
    private String institutionCode;
    private int year;
    private String name;
    // ISO date (yyyy-MM-dd) the law names
    private String statutoryDate;
    // ISO date staff are actually off - may differ (weekend rule, Executive Instrument)
    private String observedDate;
    // STATUTORY | DECLARED | SCHOOL
    private String type;
    private String note;
    // API (HolidaysMap) | RULES (built-in) | ADMIN (added or edited by a person)
    private String source;

    public PublicHoliday() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
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

    public String getStatutoryDate() {
        return statutoryDate;
    }

    public void setStatutoryDate(String statutoryDate) {
        this.statutoryDate = statutoryDate;
    }

    public String getObservedDate() {
        return observedDate;
    }

    public void setObservedDate(String observedDate) {
        this.observedDate = observedDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
