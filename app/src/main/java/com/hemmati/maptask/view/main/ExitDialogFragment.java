package com.hemmati.maptask.view.main;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hemmati.maptask.R;
import com.hemmati.maptask.base.BaseDialogFragment;

import org.jetbrains.annotations.NotNull;

public class ExitDialogFragment extends BaseDialogFragment {


    @Override
    protected int layoutRes() {
        return R.layout.fragment_exit_dialog;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnExit).setOnClickListener(v -> requireActivity().finish());
    }


}