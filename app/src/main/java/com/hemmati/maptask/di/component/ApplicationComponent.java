package com.hemmati.maptask.di.component;


import android.app.Application;


import com.hemmati.maptask.base.BaseApplication;
import com.hemmati.maptask.di.module.ActivityBindingModule;
import com.hemmati.maptask.di.module.ApplicationModule;
import com.hemmati.maptask.di.module.ContextModule;
import com.hemmati.maptask.di.module.MainFragmentBindingModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

@Singleton
@Component(modules = {ApplicationModule.class, AndroidSupportInjectionModule.class, ContextModule.class, ActivityBindingModule.class})
public interface ApplicationComponent extends AndroidInjector<BaseApplication> {

    void inject(BaseApplication application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ApplicationComponent build();
    }
}