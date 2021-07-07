package com.hemmati.maptask.di.module;


import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.hemmati.maptask.di.util.ViewModelKey;
import com.hemmati.maptask.util.ViewModelFactory;
import com.hemmati.maptask.viewModel.DirectionViewModel;
import com.hemmati.maptask.viewModel.SearchViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(DirectionViewModel.class)
    abstract ViewModel bindDirectionViewModel(DirectionViewModel directionViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
