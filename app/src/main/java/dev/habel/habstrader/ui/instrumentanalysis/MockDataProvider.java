package dev.habel.habstrader.ui.instrumentanalysis;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import yahoofinance.Utils;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

public class MockDataProvider {


    public List<HistoricalQuote> readTextFileFromRaw(Context context, int resourceId) {
        try {
            // Open the raw resource
            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(resourceId);

            // Use an InputStreamReader to read characters from the InputStream
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            // Use a BufferedReader to read lines from the InputStreamReader
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            bufferedReader.readLine();
            String line;
            List<HistoricalQuote> data = new ArrayList<>();
            // Read lines until the end of the file
            while ((line = bufferedReader.readLine()) != null) {
                // Process each line as needed
                // For example, you can print it
                HistoricalQuote quote = this.parseCSVLine(line);
                data.add(quote);
            }

            // Close the BufferedReader, InputStreamReader, and InputStream
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HistoricalQuote parseCSVLine(String line) {
        String[] data = line.split(YahooFinance.QUOTES_CSV_DELIMITER);
        return new HistoricalQuote("SBIN.NS",
                Utils.parseHistDate(data[0]),
                Utils.getBigDecimal(data[1]),
                Utils.getBigDecimal(data[3]),
                Utils.getBigDecimal(data[2]),
                Utils.getBigDecimal(data[4]),
                Utils.getBigDecimal(data[5]),
                Utils.getLong(data[6])
        );
    }
}


