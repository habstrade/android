package dev.habel.habstrader.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    private static Double charges(Double buy, Double sell, Integer qty) {
        double brokerageBuy = ((buy * qty * 0.0003) > 20 ? 20 : (buy * qty * 0.0003));
        double brokerageSell = ((sell * qty * 0.0003) > 20 ? 20 : (sell * qty * 0.0003));
        double brokerage = brokerageBuy + brokerageSell;
        double turnover = (buy + sell) * qty;
        double sttTotal = Math.round(sell * qty * 0.00025);
        double excTransCharge = 0.0000345 * turnover;
        double cc = 0;
        double stax = 0.18 * (brokerage + excTransCharge);
        double sebiCharges = turnover * 0.000001;
        sebiCharges = sebiCharges + (sebiCharges * 0.18);
        double stampCharges = (buy * qty) * 0.00003;
        double totalTax = brokerage + sttTotal + excTransCharge + cc + stax + sebiCharges + stampCharges;

        Double netProfit = ((sell - buy) * qty) - totalTax;
        System.out.printf("\n net profit " + netProfit);
        return netProfit;
    }

    private static Integer findQuantityForProfit(Double buy, Double sell, Integer profitRequired, Integer lowerBound, Integer upperBound, Integer qty) {
        Double profit = charges(buy, sell, qty);
        Integer newQty;
        if (profit.intValue() == profitRequired) {
            return closestMultiple(qty, 5);
        }
        if (profit > profitRequired) {
            upperBound = qty;

        } else {
            lowerBound = qty;
            if (qty >= upperBound) upperBound = qty * 2;
        }

        newQty = (lowerBound + upperBound) / 2;
        if ((newQty.equals(lowerBound) || newQty.equals(upperBound)) && profit < profitRequired) {
            upperBound *= 2;
        } else if (newQty.equals(lowerBound)) {
            return closestMultiple(upperBound, 5);
        } else if (newQty.equals(upperBound)) {
            return closestMultiple(lowerBound, 5);
        }

        return findQuantityForProfit(buy, sell, profitRequired, lowerBound, upperBound, newQty);
    }

    private static int closestMultiple(int n, int x) {
        if (x > n) return x;
        n = n - (n % x);
        n = n + x;
        return n;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void calculate(Double buy, Double sell, Integer qty) {
        if (buy == null) mText.setValue("Enter buy Price");
        else if (sell == null) mText.setValue("Enter sell price");
        else {
            mText.setValue(String.format(Locale.ENGLISH, "\n Required Qty for 100Rs profit: %s" + "\n\n Profit : %s" + "", findQuantityForProfit(buy, sell, 100, 0, 100, 100), charges(buy, sell, qty)));
        }
    }
}