package com.murtuza.mdp.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CsvTransport implements ITransport
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvTransport.class);
    private final String outputFilePath;

    public CsvTransport(String outputFilePath)
    {
        this.outputFilePath = outputFilePath;
        LOGGER.info("Started output transport to {}", outputFilePath);
        appendToFile("timestamp_ms,instrument,derived_value");
    }

    @Override
    public void write(long time, String symbol, double derived)
    {
        appendToFile(String.format("%d,%s,%f", time, symbol, derived));
    }

    private void appendToFile(String line)
    {
        try (FileWriter fw = new FileWriter(outputFilePath, true); BufferedWriter bw = new BufferedWriter(fw))
        {
            bw.write(line);
            bw.newLine();
        }
        catch (IOException e)
        {
            LOGGER.error("Error writing to output csv {}", outputFilePath, e);
        }
    }
}
