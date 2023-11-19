package dev.habel.habstrader.ui.instrumentanalysis;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Cartesian;

import dev.habel.habstrader.databinding.FragmentInstrumentanalyzerBinding;

public class InstrumentAnalyzerFragment extends Fragment {

    private FragmentInstrumentanalyzerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InstrumentAnalyzerViewModel instrumentanalyzerViewModel =
                new ViewModelProvider(this).get(InstrumentAnalyzerViewModel.class);

        binding = FragmentInstrumentanalyzerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);


        final AnyChartView chartView = binding.anyChartView;
        final Button btnGo = binding.btnGo;
//        new Thread(new MyRunnable(instrumentanalyzerViewModel, binding.editTxtInstrument.getText().toString(), this.getContext())).start();
////        instrumentanalyzerViewModel.updateData("SBIN.NS", this.getContext());
//        instrumentanalyzerViewModel.getChartData().observe(getViewLifecycleOwner(), new Observer<Cartesian>() {
//            @Override
//            public void onChanged(Cartesian cartesian) {
//                chartView.setChart(cartesian);
//            }
//        });

        btnGo.setOnClickListener(view -> {

            Cartesian c = instrumentanalyzerViewModel.updateData("instrument", getContext());
            chartView.setChart(c);
//            new Thread(new MyRunnable(instrumentanalyzerViewModel, binding.editTxtInstrument.getText().toString(), this.getContext())).start();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public class MyRunnable implements Runnable {
        private InstrumentAnalyzerViewModel instrumentanalyzerViewModel;
        private String instrument;
        private Context context;

        public MyRunnable(InstrumentAnalyzerViewModel instrumentanalyzerViewModel, String instrument, Context context) {


            this.instrumentanalyzerViewModel = instrumentanalyzerViewModel;
            this.instrument = instrument;
            this.context = context;
        }

        @Override
        public void run() {

            instrumentanalyzerViewModel.updateData(instrument, context);
        }
    }

    private class CustomDataEntry extends DataEntry {
        public CustomDataEntry(String x, Number edinburgHigh, Number edinburgLow, Number londonHigh, Number londonLow) {
            setValue("x", x);
            setValue("2022High", edinburgHigh);
            setValue("2022Low", edinburgLow);
            setValue("2023High", londonHigh);
            setValue("2023Low", londonLow);
        }
    }
}