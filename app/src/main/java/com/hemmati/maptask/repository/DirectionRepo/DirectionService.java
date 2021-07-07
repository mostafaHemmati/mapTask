package com.hemmati.maptask.repository.DirectionRepo;


import com.hemmati.maptask.BuildConfig;
import com.hemmati.maptask.repository.model.address.AddressModel;
import com.hemmati.maptask.repository.model.direction.DirectionModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface DirectionService {
    @Headers({BuildConfig.API_KEY})
    @GET("/v2/direction/no-traffic")
    Call<DirectionModel> getDirection(@Query("type") String type,
                                      @Query("origin") String origin,
                                      @Query("destination") String destination);
    @Headers({BuildConfig.API_KEY})
    @GET("/v2/reverse")
    Call<AddressModel> getAddress(@Query("lat") String origin,
                                  @Query("lng") String destination);
}
