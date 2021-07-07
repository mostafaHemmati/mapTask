package com.hemmati.maptask.view.search;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hemmati.maptask.R;
import com.hemmati.maptask.base.BaseDialogFragment;
import com.hemmati.maptask.repository.model.search.Item;
import com.hemmati.maptask.util.ViewModelFactory;
import com.hemmati.maptask.viewModel.SearchViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import static com.hemmati.maptask.util.Constants.LAT_KEY;
import static com.hemmati.maptask.util.Constants.LNG_KEY;
import static com.hemmati.maptask.util.Constants.SEARCH_RESULT_KEY;


public class SearchFragment extends BaseDialogFragment implements FastSearchAdapter.OnFastSearchItemListener {

    private AppCompatEditText appCompatEditText;
    private ShimmerFrameLayout shimmerViewContainer;
    private RecyclerView fastSearchRecyclerView, searchRecyclerView;
    private FloatingActionButton fabBack;

    private List<Item> items;
    private SearchAdapter adapter;

    private SearchViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_search;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initViewModel();
        initFastSearchRecyclerView();
        actionClicks();
        ViewsAction();


    }

    private void ViewsAction() {
        appCompatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.searchLocation(s.toString(),
                        Double.parseDouble(SearchFragmentArgs.fromBundle(getArguments()).getLat()),
                        Double.parseDouble(SearchFragmentArgs.fromBundle(getArguments()).getLng()));
            }
        });
    }

    private void actionClicks() {
        fabBack.setOnClickListener(v -> dismiss());
    }

    private void initView(View view) {
        fastSearchRecyclerView = view.findViewById(R.id.fastSearchRecyclerView);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        shimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        appCompatEditText = view.findViewById(R.id.appCompatEditText);
        fabBack = view.findViewById(R.id.fab_back);

        items = new ArrayList<>();

        bindAdapterToSearchRecyclerView();
    }


    private void initFastSearchRecyclerView() {
        FastSearchAdapter fastSearchAdapter = new FastSearchAdapter(getContext(),
                Arrays.asList(getResources().getStringArray(R.array.fast_search_list)),
                this);

        fastSearchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                true));

        fastSearchRecyclerView.setAdapter(fastSearchAdapter);

    }

    @Override
    public void onSearchItemClick(String searchStr) {
        hideKeyBoard();
        appCompatEditText.setText(searchStr);

    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory).get(SearchViewModel.class);

        viewModel.getItems().observe(this, items1 -> adapter.updateList(items1));

        viewModel.getProgressBarState().observe(this, state -> {
            if (state) {
                shimmerViewContainer.startShimmer();
                shimmerViewContainer.setVisibility(View.VISIBLE);
                searchRecyclerView.setVisibility(View.GONE);
            } else {
                shimmerViewContainer.stopShimmer();
                shimmerViewContainer.setVisibility(View.GONE);
                searchRecyclerView.setVisibility(View.VISIBLE);

            }
        });


    }

    private void bindAdapterToSearchRecyclerView() {
        adapter = new SearchAdapter(items, latLng -> {

            NavController navController = NavHostFragment.findNavController(this);
            Bundle bundle = new Bundle();
            bundle.putDouble(LAT_KEY, latLng.getLatitude());
            bundle.putDouble(LNG_KEY, latLng.getLongitude());
            Objects.requireNonNull(navController.getPreviousBackStackEntry()).getSavedStateHandle().set(SEARCH_RESULT_KEY, bundle);
            dismiss();
        });
        searchRecyclerView.setAdapter(adapter);
    }


    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (requireActivity().getCurrentFocus() != null) {
            Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(
                    requireActivity().getCurrentFocus()).getWindowToken(), 0);
        }
    }

}