package dev.habel.habstrader.ui.instrumentanalysis;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.List;

import yahoofinance.histquotes.HistoricalQuote;

public class QuoteSummary {
    private final BigDecimal maxHigh;
    private final BigDecimal maxLow;
    private final BigDecimal minHigh;
    private final BigDecimal minLow;

    public QuoteSummary(BigDecimal maxHigh, BigDecimal maxLow, BigDecimal minHigh, BigDecimal minLow) {

        this.maxHigh = maxHigh;
        this.maxLow = maxLow;
        this.minHigh = minHigh;
        this.minLow = minLow;
    }

    public static QuoteSummary calculateSummary(List<HistoricalQuote> historicalQuotes) {
        BigDecimal maxHigh = historicalQuotes.stream()
                .map(HistoricalQuote::getHigh)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal minHigh = historicalQuotes.stream()
                .map(HistoricalQuote::getHigh)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxLow = historicalQuotes.stream()
                .map(HistoricalQuote::getLow)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal minLow = historicalQuotes.stream()
                .map(HistoricalQuote::getLow)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return new QuoteSummary(maxHigh, maxLow, minHigh, minLow);
    }

    public BigDecimal getMaxHigh() {
        return maxHigh;
    }

    public BigDecimal getMaxLow() {
        return maxLow;
    }

    public BigDecimal getMinHigh() {
        return minHigh;
    }

    public BigDecimal getMinLow() {
        return minLow;
    }

    @NonNull
    @Override
    public String toString() {
        return "HH: " + getMaxHigh() +
               ", HL: " + getMaxLow() +
               ", LH: " + getMinHigh() +
               ", LL: " + getMinLow();
    }
}