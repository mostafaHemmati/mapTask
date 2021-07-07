package com.hemmati.maptask.di.module;

import com.hemmati.maptask.view.main.ExitDialogFragment;
import com.hemmati.maptask.view.main.MainFragment;
import com.hemmati.maptask.view.search.SearchFragment;
import com.hemmati.maptask.view.splash.SplashFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBindingModule {

    @ContributesAndroidInjector
    abstract MainFragment provideMainFragment();

    @ContributesAndroidInjector
    abstract SearchFragment provideSearchFragment();

    @ContributesAndroidInjector
    abstract SplashFragment provideSplashFragment();

    @ContributesAndroidInjector
    abstract ExitDialogFragment provideExitFragment();
}