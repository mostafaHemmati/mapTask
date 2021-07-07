package com.hemmati.maptask.viewModel;

import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.carto.graphics.Color;
import com.carto.styles.LineStyle;
import com.carto.styles.LineStyleBuilder;
import com.hemmati.maptask.repository.DirectionRepo.DirectionRepository;
import com.hemmati.maptask.repository.model.address.AddressModel;
import com.hemmati.maptask.repository.model.direction.DirectionModel;
import com.hemmati.maptask.repository.model.direction.Route;
import com.hemmati.maptask.repository.model.direction.Step;
import com.hemmati.maptask.util.SingleLiveEvent;

import org.jetbrains.annotations.NotNull;
import org.neshan.common.model.LatLng;
import org.neshan.common.utils.PolylineEncoding;
import org.neshan.mapsdk.model.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hemmati.maptask.view.main.MainFragment.CAR_KEY;

public class DirectionViewModel extends ViewModel {
    private final SingleLiveEvent<Polyline> polylineMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> addressMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<List<LatLng>> stepsMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> durationMutableLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> distanceKmMutableLiveData = new SingleLiveEvent<>();

    private final DirectionRepository directionRepository;
    private Color lineColor;

    @Inject
    public DirectionViewModel(DirectionRepository directionRepository) {
        this.directionRepository = directionRepository;
    }

    public void getAddress(double lat, double lng) {
        directionRepository.getAddress(String.valueOf(lat), String.valueOf(lng)).enqueue(new Callback<AddressModel>() {
            @Override
            public void onResponse(@NotNull Call<AddressModel> call, @NotNull Response<AddressModel> response) {
                AddressModel addressModel = response.body();
                if (!TextUtils.isEmpty(Objects.requireNonNull(addressModel).getFormattedAddress()))
                    addressMutableLiveData.setValue(addressModel.getFormattedAddress());
            }

            @Override
            public void onFailure(@NotNull Call<AddressModel> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getDirection(String type, String origin, String destination) {
        directionRepository.getDirection(type, origin, destination).enqueue(new Callback<DirectionModel>() {
            @Override
            public void onResponse(@NotNull Call<DirectionModel> call, @NotNull Response<DirectionModel> response) {
                DirectionModel directionModel = response.body();
                Route route = Objects.requireNonNull(directionModel).getRoutes().get(0);
                ArrayList<LatLng> routeOverviewPolylinePoints = new ArrayList<>(PolylineEncoding.decode(route.getOverviewPolyline().getPoints()));
                ArrayList<LatLng> stepByStepPath = new ArrayList<>();
                for (Step step : route.getLegs().get(0).getSteps()) {
                    stepByStepPath.addAll(PolylineEncoding.decode(step.getPolyline()));
                }
                distanceKmMutableLiveData.setValue(route.getLegs().get(0).getDistance().getText());
                durationMutableLiveData.setValue(route.getLegs().get(0).getDuration().getText());
                stepsMutableLiveData.setValue(stepByStepPath);

                Polyline onMapPolyline = new Polyline(routeOverviewPolylinePoints, getLineStyle());
                polylineMutableLiveData.setValue(onMapPolyline);
            }

            @Override
            public void onFailure(@NotNull Call<DirectionModel> call, @NotNull Throwable t) {
                t.printStackTrace();
                lineColor = null;

            }
        });
    }

    public void getNavigateRequest(String origin, String destination) {

        getDirection(CAR_KEY, origin, destination);
    }


    private LineStyle getLineStyle() {
        lineColor = new Color((short) 2, (short) 119, (short) 189, (short) 190);
        LineStyleBuilder lineStCr = new LineStyleBuilder();
        lineStCr.setColor(lineColor);
        lineStCr.setWidth(10f);
        lineStCr.setStretchFactor(5f);
        return lineStCr.buildStyle();
    }

    public SingleLiveEvent<Polyline> getPolyLine() {
        return polylineMutableLiveData;
    }

    public SingleLiveEvent<String> getAddress() {
        return addressMutableLiveData;
    }

    public LiveData<List<LatLng>> getStep() {
        return stepsMutableLiveData;
    }

    public LiveData<String> getDuration() {
        return durationMutableLiveData;
    }

    public LiveData<String> getDistanceKm() {
        return distanceKmMutableLiveData;
    }
}
