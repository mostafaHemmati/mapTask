package com.hemmati.maptask.di.module;



import com.hemmati.maptask.BuildConfig;
import com.hemmati.maptask.repository.DirectionRepo.DirectionService;
import com.hemmati.maptask.repository.SearchRepo.SearchService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class ApplicationModule {


    @Singleton
    @Provides
    static Retrofit provideRetrofit() {
        return new Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    @Singleton
    @Provides
    static DirectionService provideDirectionRetrofitService(Retrofit retrofit) {
        return retrofit.create(DirectionService.class);
    }

    @Singleton
    @Provides
    static SearchService provideSearchRetrofitService(Retrofit retrofit) {
        return retrofit.create(SearchService.class);
    }

}
