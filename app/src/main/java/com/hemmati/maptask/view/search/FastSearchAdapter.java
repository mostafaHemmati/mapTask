package com.hemmati.maptask.view.search;


import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hemmati.maptask.R;

import java.util.List;

public class FastSearchAdapter extends RecyclerView.Adapter<FastSearchAdapter.ViewHolder> {


    private final List<String> items;
    private final Context context;
    private final OnFastSearchItemListener onFastSearchItemListener;

    public FastSearchAdapter(Context context, List<String> items, OnFastSearchItemListener onFastSearchItemListener) {
        this.items = items;
        this.context = context;
        this.onFastSearchItemListener = onFastSearchItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fast_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0)
            ViewCompat.setBackgroundTintList(holder.fab, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.darkRed)));


        holder.fab.setImageDrawable(ContextCompat.getDrawable(context, getImageRes(items.get(position))));

    }

    private int getImageRes(String s) {
        switch (s) {
            case "بیمارستان":
                return R.drawable.ic_hospital;
            case "رستوران":
                return R.drawable.ic_resturant;
            case "پمپ بنزین":
                return R.drawable.ic_gas;
            case "مرکز خرید":
                return R.drawable.ic_shop;
            case "دانشگاه":
                return R.drawable.ic_school;
            case "سرویس بهداشتی":
                return R.drawable.ic_wc;
            case "پارکینگ":
                return R.drawable.ic_parking;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final FloatingActionButton fab;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fab = itemView.findViewById(R.id.fab);

            fab.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            animateFab(v);
            onFastSearchItemListener.onSearchItemClick(items.get(getAdapterPosition()));

        }
    }

    private void animateFab(View v) {
        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn(v);
    }

    public interface OnFastSearchItemListener {
        void onSearchItemClick(String searchStr);
    }
}
