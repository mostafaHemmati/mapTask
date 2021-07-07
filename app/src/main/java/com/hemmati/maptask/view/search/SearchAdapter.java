package com.hemmati.maptask.view.search;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.daimajia.androidanimations.library.YoYo;
import com.hemmati.maptask.R;
import com.hemmati.maptask.repository.model.search.Item;
import com.hemmati.maptask.repository.model.search.Location;

import org.neshan.common.model.LatLng;

import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {


    private List<Item> items;
    private OnSearchItemListener onSearchItemListener;

    public SearchAdapter(List<Item> items, OnSearchItemListener onSearchItemListener) {
        this.items = items;
        this.onSearchItemListener = onSearchItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText(items.get(position).getTitle());
        holder.tvAddress.setText(items.get(position).getAddress());
        holder.tvType.setText(items.get(position).getNeighbourhood());
//        holder.tvKmNum.setText(items.get(position).getRegion());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public void updateList(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvTitle;
        private final TextView tvAddress;
        private final TextView tvType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textView_title);
            tvAddress = itemView.findViewById(R.id.textView_address);
            tvType = itemView.findViewById(R.id.textView_type);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Location location = items.get(getAdapterPosition()).getLocation();
            LatLng LatLng = new LatLng(location.getY(), location.getX());
            onSearchItemListener.onSearchItemClick(LatLng);
        }
    }

    public interface OnSearchItemListener {
        void onSearchItemClick(LatLng latLng);
    }
}
