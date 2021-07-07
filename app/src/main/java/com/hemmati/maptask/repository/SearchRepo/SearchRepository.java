package com.hemmati.maptask.repository.SearchRepo;



import com.hemmati.maptask.repository.model.search.SearchModel;

import javax.inject.Inject;

import retrofit2.Call;

public class SearchRepository {
    private final SearchService searchService;


    @Inject
    public SearchRepository(SearchService searchService) {
        this.searchService = searchService;
    }

    public Call<SearchModel> getSearch(String term, double lat, double lng) {
        return searchService.getSearch(term, lat, lng);
    }
}
