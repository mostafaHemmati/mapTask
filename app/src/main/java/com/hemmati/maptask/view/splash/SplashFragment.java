package com.hemmati.maptask.view.splash;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.hemmati.maptask.R;
import com.hemmati.maptask.base.BaseFragment;

import org.jetbrains.annotations.NotNull;


public class SplashFragment extends BaseFragment {
    private static final int SPLASH_SCREEN_DELAY = 3000;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_splash;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(() -> {
            NavDirections action =
                    SplashFragmentDirections
                            .actionSplashFragmentToMainFragment("","");
            Navigation.findNavController(view).navigate(action);
        }, SPLASH_SCREEN_DELAY);

    }
}