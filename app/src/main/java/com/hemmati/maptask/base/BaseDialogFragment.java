package com.hemmati.maptask.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dagger.android.support.DaggerDialogFragment;
import dagger.android.support.DaggerFragment;

public abstract class BaseDialogFragment extends DaggerDialogFragment {

    private AppCompatActivity activity;

    @LayoutRes
    protected abstract int layoutRes();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutRes(), container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public AppCompatActivity getBaseActivity() {
        return activity;
    }

}