package dev.habel.habstrader.ui.instrumentanalysis;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.RangeColumn;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import dev.habel.habstrader.R;
import lombok.Getter;
import yahoofinance.histquotes.HistoricalQuote;

public class InstrumentAnalyzerViewModel extends ViewModel {
    @Getter
    private final MutableLiveData<Cartesian> chartData;
    private final Calendar from = Calendar.getInstance();
    DateTimeFormatter parser;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Cartesian cartesian;

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            parser = DateTimeFormatter.ofPattern("MMM").withLocale(Locale.ENGLISH);
        }

    }

    public InstrumentAnalyzerViewModel() {
        System.setProperty("yahoofinance.quotesquery1v7.enabled", "false");
        System.setProperty("yahoofinance.cookie", "A1=d=AQABBDrCWGUCEMQtbfmBt4ibNNWZpCpswfgFEgEBAQETWmViZVkTyyMA_eMAAA&S=AQAAAomd1ZoJDVEE5iAiJzxjhcY; A3=d=AQABBDrCWGUCEMQtbfmBt4ibNNWZpCpswfgFEgEBAQETWmViZVkTyyMA_eMAAA&S=AQAAAomd1ZoJDVEE5iAiJzxjhcY; A1S=d=AQABBDrCWGUCEMQtbfmBt4ibNNWZpCpswfgFEgEBAQETWmViZVkTyyMA_eMAAA&S=AQAAAomd1ZoJDVEE5iAiJzxjhcY; cmp=t=1700319310&j=0&u=1---; gpp=DBAA; gpp_sid=-1; PRF=t%3DSBIN.NS%26newChartbetateaser%3D0%252C1701525311966; axids=gam=y-zVReI4BE2uLCBAuxTvhYaRqAf3S7sYE2~A&dv360=eS1CQU53NWR4RTJ1R0IyaHQwLmQ2ZWdnUjlLOHZudzMwWn5B; gam_id=y-zVReI4BE2uLCBAuxTvhYaRqAf3S7sYE2~A");
        System.setProperty("yahoofinance.crumb", "A");
        chartData = new MutableLiveData<>();
        from.add(Calendar.YEAR, -10); // from 10 years ago
        System.setProperty("yahoofinance.scrapeurl.histquotes2", "https://finance.yahoo.com/quote/SBIN.NS/history?p=SBIN.NS");
        System.setProperty("yahoofinance.baseurl.histquotes2", "https://query1.finance.yahoo.com/v7/finance/download/");
//        System.setProperty("yahoofinance.baseurl.histquotes2", "https://query2.finance.yahoo.com/v8/finance/chart/" );
    }

    private static double calculatePercentage(BigDecimal value, BigDecimal summaryHigh, BigDecimal summaryLow) {
        double valFinal = value.doubleValue() - summaryLow.doubleValue();
        double highFinal = summaryHigh.doubleValue() - summaryLow.doubleValue();
        return (valFinal / highFinal) * 100.0;
    }

    public MutableLiveData<Cartesian> getChartData() {
        if (cartesian == null) {
            instantiateSetMonthWise(new ArrayList<>(), new HashSet<Integer>());
        }
        return chartData;
    }

    public Cartesian updateData(String instrument, Context context) {

        List<HistoricalQuote> historicalQuotes = new MockDataProvider().readTextFileFromRaw(context, R.raw.sbin);
//                .stream().filter(h -> h.getDate().get(Calendar.YEAR) >= 2021).filter(h -> h.getDate().get(Calendar.MONTH) < 3).collect(Collectors.toList());

        getData(historicalQuotes);
//        cartesian.title("Month wise data for " + instrument);
        return cartesian;
    }

    public List<DataEntry> getData(List<HistoricalQuote> historicalQuotes) {
        Map<String, List<HistoricalQuote>> groupedByMonth = historicalQuotes.stream()
                .collect(Collectors.groupingBy(obj -> obj.getDate().getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())));
        try {
            System.out.println(new ObjectMapper().writeValueAsString(historicalQuotes));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Grouping by year and calculating high and low values
        Map<Integer, QuoteSummary> summaryByYear = historicalQuotes.stream()
                .collect(Collectors.groupingBy(
                        quote -> quote.getDate().get(Calendar.YEAR),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                QuoteSummary::calculateSummary)));


        HashSet<Integer> distinctYear = new HashSet<>();
        List<DataEntry> seriesData = new ArrayList<>();
        groupedByMonth.forEach((month, data) -> {
            CustomDataEntry monthlyData = new CustomDataEntry(month);
            for (HistoricalQuote historicalQuote : data) {
                Integer year = historicalQuote.getDate().get(Calendar.YEAR);
                distinctYear.add(year);

                QuoteSummary yearSummary = summaryByYear.get(year);
                if (yearSummary != null) {
                    double highPercentage = calculatePercentage(historicalQuote.getHigh(), yearSummary.getMaxHigh(), yearSummary.getMinHigh());
                    double lowPercentage = calculatePercentage(historicalQuote.getLow(), yearSummary.getMaxLow(), yearSummary.getMinLow());
                    System.out.println(yearSummary + "  " + historicalQuote);
                    System.out.println(highPercentage + "  " + lowPercentage);
                    monthlyData.setValue("High" + year, (highPercentage + lowPercentage) / 2);
                    monthlyData.setValue("Low" + year, (highPercentage + lowPercentage) / 2);
                }


                monthlyData.setValue("Mid" + year, (historicalQuote.getHigh().add(historicalQuote.getLow()).divide(new BigDecimal("2"))));

            }
            seriesData.add(monthlyData);
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seriesData.sort((t1, t2) -> parser.parse((String) t1.getValue("x")).get(ChronoField.MONTH_OF_YEAR) - parser.parse((String) t2.getValue("x")).get(ChronoField.MONTH_OF_YEAR));
        }
        instantiateSetMonthWise(seriesData, distinctYear);
        chartData.setValue(cartesian);
//        handler.post(() -> chartData.setValue(cartesian));
        return seriesData;
    }

    private void instantiateSetMonthWise(List<DataEntry> seriesData, HashSet<Integer> distinctYear) {
        if (cartesian == null) {
            cartesian = AnyChart.cartesian();
            cartesian.xAxis(true);
            cartesian.yAxis(true);

            cartesian.yScale()
                    .minimum(0d)
                    .maximum(100d);

            cartesian.legend(true);

            cartesian.yGrid(true)
                    .yMinorGrid(true);

            cartesian.tooltip().titleFormat("{%SeriesName} ({%x})");

        }
//        int year = Calendar.getInstance().get(Calendar.YEAR);
        Set set = Set.instantiate();
        set.data(seriesData);
        for (Integer year : distinctYear) {
            Mapping mapping = set.mapAs("{ x: 'x', high: 'High" + year + "', low: 'Low" + year + "' }");
            RangeColumn rangeColumn = cartesian.rangeColumn(mapping);
            rangeColumn.name(String.valueOf(year));
        }

        cartesian.xAxis(true);
        cartesian.yAxis(true);

        cartesian.yScale()
                .minimum(-50d)
                .maximum(150d);

        cartesian.legend(true);

        cartesian.yGrid(true)
                .yMinorGrid(true);

        cartesian.tooltip().titleFormat("{%SeriesName} ({%x})");

    }

    private class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String x) {
            setValue("x", x);
        }

    }
}