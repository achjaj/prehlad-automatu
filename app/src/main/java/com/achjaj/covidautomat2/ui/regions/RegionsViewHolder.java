package com.achjaj.covidautomat2.ui.regions;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.achjaj.covidautomat2.R;

public class RegionsViewHolder extends RecyclerView.ViewHolder {
    private View view;

    public RegionsViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
    }

    protected TextView getLabel() {
        return view.findViewById(R.id.region_title);
    }
}
