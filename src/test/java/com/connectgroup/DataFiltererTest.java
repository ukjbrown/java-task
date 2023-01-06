package com.connectgroup;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Instant;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DataFiltererTest
{
    @Test
    public void shouldReturnEmptyList_WhenLogFileIsEmpty() throws FileNotFoundException
    {
        assertTrue(DataFilterer.filterByCountry(openFile("src/test/resources/empty"), "GB").isEmpty());
    }

    @Test
    public void shouldReturnFilteredListWhenCountryCodeSupplied() throws FileNotFoundException
    {
        RequestLogEntry expectedEntry1 = RequestLogEntry.fromRequestLogLine("1432484176,US,850");
        RequestLogEntry expectedEntry2 = RequestLogEntry.fromRequestLogLine("1433190845,US,539");
        RequestLogEntry expectedEntry3 = RequestLogEntry.fromRequestLogLine("1433666287,US,789");

        List<RequestLogEntry> expected = List.of(expectedEntry1, expectedEntry2, expectedEntry3);

        List<RequestLogEntry> actual = DataFilterer.filterByCountry(openFile("src/test/resources/multi-lines"), "US");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnFilteredListWhenCountryCodeAndLimitSupplied() throws FileNotFoundException
    {
        RequestLogEntry expectedEntry1 = RequestLogEntry.fromRequestLogLine("1432484176,US,850");
        RequestLogEntry expectedEntry2 = RequestLogEntry.fromRequestLogLine("1433666287,US,789");

        List<RequestLogEntry> expected = List.of(expectedEntry1, expectedEntry2);

        List<RequestLogEntry> actual = DataFilterer.filterByCountryWithResponseTimeAboveLimit(
                openFile("src/test/resources/multi-lines"), "US", 600);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnFilteredListForAboveAverage() throws FileNotFoundException
    {
        // average = 526

        RequestLogEntry expectedEntry1 = RequestLogEntry.fromRequestLogLine("1432484176,US,850");
        RequestLogEntry expectedEntry2 = RequestLogEntry.fromRequestLogLine("1433190845,US,539");
        RequestLogEntry expectedEntry3 = RequestLogEntry.fromRequestLogLine("1433666287,US,789");

        List<RequestLogEntry> expected = List.of(expectedEntry1, expectedEntry2, expectedEntry3);

        List<RequestLogEntry> actual = DataFilterer.filterByResponseTimeAboveAverage(openFile("src/test/resources/multi-lines"));

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void requestLogEntryShouldBuildFromRequestLogLine()
    {
        long timestamp = 1433190845;
        int responseTime = 1000;

        RequestLogEntry expected = new RequestLogEntry(Instant.ofEpochSecond(timestamp), "GB", responseTime);

        String requestLogLine = String.format("%d,%s,%d", timestamp, "GB", responseTime);

        RequestLogEntry actual = RequestLogEntry.fromRequestLogLine(requestLogLine);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldWorkWithLargeDataset() throws FileNotFoundException
    {
        int expectedCount = 348;
        List<RequestLogEntry> actual = DataFilterer.filterByCountry(openFile("src/test/resources/large-dataset"), "US");

        Assert.assertEquals(actual.size(), expectedCount);
    }

    private FileReader openFile(String filename) throws FileNotFoundException
    {
        return new FileReader(filename);
    }
}
