package com.achjaj.covidautomat2.ui.regions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.achjaj.covidautomat2.R;

import java.util.List;
import java.util.stream.Collectors;

public class RegionsAdapter extends RecyclerView.Adapter<RegionsViewHolder> {
    private List<String> regions;

    public RegionsAdapter(List<String> regions) {
        this.regions = regions;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0)
                results.values = RegionsFragment.getRegions();
            else
                results.values = regions.stream()
                    .filter(r -> r.contains(constraint))
                    .collect(Collectors.toList());

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            regions.clear();
            regions.addAll((List<String>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public RegionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.region_item_layout, parent, false);

        return new RegionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegionsViewHolder holder, int position) {
        holder.getLabel().setText(regions.get(position));
    }

    @Override
    public int getItemCount() {
        return regions.size();
    }

}
