package com.connectgroup;

import java.time.Instant;
import java.util.Objects;

/**
 * Representation of a Request Log entry.
 *
 * Assumptions:
 *  - It would be useful for the request timestamp to be stored as a temporal type.
 *  - Country code could have been converted to an ISOCountryCode type, however the required data filter method passes
 *    in a String, so String seems more appropriate for this implementation.
 */
public class RequestLogEntry
{
    private final Instant requestTimestamp;
    private final String countryCode;
    private final int responseTime;

    public RequestLogEntry(Instant requestTimestamp, String countryCode, int responseTime)
    {
        this.requestTimestamp = requestTimestamp;
        this.countryCode = countryCode;
        this.responseTime = responseTime;
    }

    /**
     * Build a RequestLogEntry from request log line
     *
     * @param line request log line from file
     * @return built RequesdtLogEntry
     * @throws Exception
     */
    public static RequestLogEntry fromRequestLogLine(String line)
    {
        String[] parts = line.split(",");

        Instant requestTimestamp = Instant.ofEpochSecond(Long.valueOf(parts[0]));
        String countryCode = parts[1];
        int responseTime = Integer.valueOf(parts[2]);

        return new RequestLogEntry(requestTimestamp, countryCode, responseTime);
    }

    public Instant getRequestTimestamp()
    {
        return requestTimestamp;
    }

    public String getCountryCode()
    {
        return countryCode;
    }

    public int getResponseTime()
    {
        return responseTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLogEntry that = (RequestLogEntry) o;
        return responseTime == that.responseTime && requestTimestamp.equals(that.requestTimestamp) && countryCode.equals(that.countryCode);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(requestTimestamp, countryCode, responseTime);
    }
}
