package com.hemmati.maptask.repository.DirectionRepo;


import com.hemmati.maptask.repository.model.address.AddressModel;
import com.hemmati.maptask.repository.model.direction.DirectionModel;

import javax.inject.Inject;

import retrofit2.Call;

public class DirectionRepository {
    private final DirectionService directionService;


    @Inject
    public DirectionRepository(DirectionService directionService) {
        this.directionService = directionService;
    }


    public Call<DirectionModel> getDirection(String type, String origin, String destination) {
        return directionService.getDirection(type, origin, destination);
    }

    public Call<AddressModel> getAddress(String lat, String lng) {
        return directionService.getAddress(lat, lng);
    }
}
