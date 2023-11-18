package dev.habel.habstrader.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import dev.habel.habstrader.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final TextView txtQuantity = binding.txtQuantity;
        final TextView txtBuy = binding.txtBuy;
        final TextView txtSell = binding.txtSell;

        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Double qty = parse(txtQuantity.getText());
                homeViewModel.calculate(parse(txtBuy.getText()), parse(txtSell.getText()), qty == null ? 0 : qty.intValue());
            }

            private Double parse(CharSequence cs) {
                if (cs.length() == 0) return null;
                return Double.valueOf(cs.toString());
            }
        };
//        txtBuy.addTextChangedListener(textWatcher);
//        txtSell.addTextChangedListener(textWatcher);
        txtQuantity.addTextChangedListener(textWatcher);
        homeViewModel.calculate(null, null, null);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}