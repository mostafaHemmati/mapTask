package com.hemmati.maptask.viewModel;

import androidx.lifecycle.ViewModel;

import com.hemmati.maptask.repository.SearchRepo.SearchRepository;
import com.hemmati.maptask.repository.model.search.Item;
import com.hemmati.maptask.repository.model.search.SearchModel;
import com.hemmati.maptask.util.SingleLiveEvent;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private final SingleLiveEvent<List<Item>> itemSingleLiveEvent = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> showLoading = new SingleLiveEvent<>();

    private final SearchRepository searchRepository;


    @Inject
    public SearchViewModel(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public void searchLocation(String term, double lat, double lng) {
        showLoading.setValue(true);
        searchRepository.getSearch(term, lat, lng).enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(@NotNull Call<SearchModel> call, @NotNull Response<SearchModel> response) {
                if (response.isSuccessful())
                    itemSingleLiveEvent.setValue(Objects.requireNonNull(response.body()).getItems());
                showLoading.setValue(false);

            }

            @Override
            public void onFailure(@NotNull Call<SearchModel> call, @NotNull Throwable t) {
                t.printStackTrace();
                showLoading.setValue(false);

            }
        });
    }

    public SingleLiveEvent<List<Item>> getItems() {
        return itemSingleLiveEvent;
    }

    public SingleLiveEvent<Boolean> getProgressBarState() {
        return showLoading;
    }
}
