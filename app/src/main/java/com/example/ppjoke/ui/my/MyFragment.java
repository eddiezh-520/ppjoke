package com.example.ppjoke.ui.my;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.libnavannotation.FragmentDestination;
import com.example.ppjoke.databinding.FragmentHomeBinding;
import com.example.ppjoke.databinding.FragmentMyBinding;
import com.example.ppjoke.ui.home.HomeViewModel;

@FragmentDestination(pageUrl = "main/tabs/my",needLogin = true)
public class MyFragment extends Fragment {

    private static final String TAG = "MyFragment";
    private MyViewModel myViewModel;
    private FragmentMyBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myViewModel =
                new ViewModelProvider(this).get(MyViewModel.class);

        binding = FragmentMyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textMy;
        myViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        Log.d(TAG,"onCreateView");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}