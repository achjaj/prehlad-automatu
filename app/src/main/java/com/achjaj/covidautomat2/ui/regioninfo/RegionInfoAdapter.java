package com.achjaj.covidautomat2.ui.regioninfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.achjaj.covidautomat2.R;
import org.achjaj.covid.Action;
import org.achjaj.covid.Category;
import org.achjaj.covid.CovidAutomat;

import java.util.List;
import java.util.stream.Collectors;

public class RegionInfoAdapter extends RecyclerView.Adapter<RegionInfoViewHolder> {
    List<Action> allActions;

    public RegionInfoAdapter(List<Action> allActions) {
        this.allActions = allActions;
    }

    @NonNull
    @Override
    public RegionInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.layout_category, parent, false);

        return new RegionInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegionInfoViewHolder holder, int position) {
        Category category = Category.values()[position];
        List<Action> filtered = allActions.stream()
            .filter(action -> action.getCategory() == category)
            .collect(Collectors.toList());

        holder.showActions(filtered, category);
    }

    @Override
    public int getItemCount() {
        return Category.values().length;
    }
}
