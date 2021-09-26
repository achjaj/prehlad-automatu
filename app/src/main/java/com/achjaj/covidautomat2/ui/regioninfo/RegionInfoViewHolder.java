package com.achjaj.covidautomat2.ui.regioninfo;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.achjaj.covidautomat2.R;
import com.achjaj.covidautomat2.Utils;
import org.achjaj.covid.Action;
import org.achjaj.covid.Category;
import org.achjaj.covid.Icon;

import java.util.List;

public class RegionInfoViewHolder extends RecyclerView.ViewHolder {
    private View view;
    private Category categoryInUse;

    public RegionInfoViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
    }

    public void showActions(List<Action> actions, Category category) {
        if (category == categoryInUse)
            return;

        categoryInUse = category;
        ((TextView) view.findViewById(R.id.category_label)).setText(category.label());
        LinearLayout actionsHolder = view.findViewById(R.id.actions_holder);
        LayoutInflater inflater = LayoutInflater.from(view.getContext());

        actions.forEach(action -> {
            View actionView = inflater.inflate(R.layout.layout_action, actionsHolder, false);

            ((TextView) actionView.findViewById(R.id.action_label)).setText(action.getLabel());
            ((TextView) actionView.findViewById(R.id.action_text)).setText(action.getText());

            int icon = (action.getIcon() == Icon.GREEN || action.getIcon() == Icon.GREEN_INFO) ? R.drawable.ic_icon_green : R.drawable.ic_icon_alert;
            ((ImageView) actionView.findViewById(R.id.action_icon)).setImageResource(icon);

            if (action.getDetail() != null && !action.getDetail().equals("")) {
                actionView.setOnClickListener(v -> Utils.showLinkClickableTextDialog(view.getContext(), action.getDetail()));
                actionView.findViewById(R.id.info_indicator).setVisibility(View.VISIBLE);
            }

            actionsHolder.addView(actionView);
        });
    }

}
