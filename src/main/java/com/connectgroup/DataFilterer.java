package com.connectgroup;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataFilterer
{
    /**
     * Filter request log by country code (additional assumption: ordered by timestamp)
     *
     * @param source request log file reader
     * @param country country code to filter by
     * @return list of request log entries
     */
    public static List<RequestLogEntry> filterByCountry(Reader source, String country)
    {
        return filter(source, Optional.of(country), null);
    }

    /**
     * Filter request log by country code and where response time is above/equal to a limit
     *
     * @param source request log file reader
     * @param country country code to filter by
     * @param limit response time to find entries over
     * @return list of request log entries
     */
    public static List<RequestLogEntry> filterByCountryWithResponseTimeAboveLimit(Reader source, String country, long limit)
    {
        return filter(source,
                Optional.of(country),
                requestLogEntry -> requestLogEntry.getResponseTime() >= limit);
    }

    /**
     * Filter request log where response time is above average
     *
     * @param source request log file reader
     * @return list of request log entries
     */
    public static List<RequestLogEntry> filterByResponseTimeAboveAverage(Reader source)
    {
        // Pull all entries into list
        List<RequestLogEntry> unfilteredEntries = filter(source, Optional.empty(), null);

        // Calculate average response time
        double averageResponseTime = unfilteredEntries
                .stream()
                .mapToDouble(requestLogEntry -> requestLogEntry.getResponseTime())
                .average()
                .orElse(0);

        // Filter by average response time, sort, and collect as list
        List<RequestLogEntry> filteredEntries = unfilteredEntries
                .stream()
                .filter(requestLogEntry -> requestLogEntry.getResponseTime() > averageResponseTime)
                .sorted(Comparator.comparing(RequestLogEntry::getRequestTimestamp))
                .collect(Collectors.toList());

        return filteredEntries;
    }

    /**
     * Generic filter method to encapsulate shared logic from available filter methods
     *
     * @param source request log file reader
     * @param countryCode country code to filter by
     * @param predicate predicate to apply to request log entries
     * @return
     */
    private static List<RequestLogEntry> filter(Reader source, Optional<String> countryCode, Predicate<RequestLogEntry> predicate)
    {
        // Create new buffered reader from file reader
        BufferedReader bufferedReader = new BufferedReader(source);

        // Build line validator regex
        String countryCodePart = countryCode.orElse("[A-Z][A-Z]");
        Pattern validator = Pattern.compile("[0-9]*," + countryCodePart + ",[0-9]*");

        // Get stream of lines
        Stream<String> lines = bufferedReader.lines();

        // Skip header, filter on valid lines and map to RequestLogEntry class
        Stream<RequestLogEntry> stream = lines
                .skip(1)
                .filter(line -> validator.matcher(line).matches())
                .map(RequestLogEntry::fromRequestLogLine);

        // Apply predicate if supplied
        if(predicate != null) stream = stream.filter(predicate);

        // Sort and collect as list
        List<RequestLogEntry> toReturn = stream
                .sorted(Comparator.comparing(RequestLogEntry::getRequestTimestamp))
                .collect(Collectors.toList());

        return toReturn;
    }
}
