package com.hemmati.maptask.repository.SearchRepo;


import com.hemmati.maptask.BuildConfig;
import com.hemmati.maptask.repository.model.search.SearchModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface SearchService {
    @Headers(BuildConfig.API_KEY)
    @GET("/v1/search")
    Call<SearchModel> getSearch(@Query("term") String term,
                                @Query("lat") Double lat,
                                @Query("lng") Double lng);
}
