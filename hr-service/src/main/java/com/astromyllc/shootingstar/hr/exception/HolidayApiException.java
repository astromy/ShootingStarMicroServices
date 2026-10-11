package com.astromyllc.shootingstar.hr.exception;

/**
 * HolidaysMap could not give us holidays. The message is written for the person
 * who reads it on the Holidays screen and never contains the API key (the key
 * travels in the request URL, so exception text from the HTTP client is never reused).
 */
public class HolidayApiException extends RuntimeException {

    public enum Kind {
        NOT_CONFIGURED,     // no API key set
        KEY_REJECTED,       // 401
        QUOTA,              // 429
        RANGE_UNAVAILABLE,  // the plan doesn't allow start/end date ranges
        UNAVAILABLE,        // network error, timeout or a 5xx
        BAD_RESPONSE        // 200 but not holidays we can use
    }

    private final Kind kind;

    public HolidayApiException(Kind kind, String message) {
        super(message);
        this.kind = kind;
    }

    public Kind getKind() {
        return kind;
    }
}
