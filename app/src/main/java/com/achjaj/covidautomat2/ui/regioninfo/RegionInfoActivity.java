package com.achjaj.covidautomat2.ui.regioninfo;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.achjaj.covidautomat2.R;
import com.achjaj.covidautomat2.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import org.achjaj.covid.*;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RegionInfoActivity extends AppCompatActivity {
    private Region region;
    private boolean currentPeriod = true;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d. M.", Locale.getDefault());

    private Utils.ICallback callback = (region, e) -> {
        runOnUiThread(() -> {
            if (e == null) {
                this.region = region;
                ((TextView) findViewById(R.id.region_name)).setText(region.getRegion());
                showPeriod(currentPeriod ? region.getCurrentPeriod() : region.getNextPeriod());

                findViewById(R.id.info_bar).setVisibility(View.GONE);
            } else {
                Utils.showExceptionDialog(getString(R.string.internet_question), e, this,
                    (dialog, which) -> finish());
            }
        });
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region_info);

        findViewById(R.id.fab).setOnClickListener(this::fabAction);

        Bundle extras = getIntent().getExtras();
        currentPeriod = !extras.getBoolean("nextWeek");

        if (extras.containsKey("regionName")) {
            Utils.getRegionByName(extras.getString("regionName"), callback);
        } else if (extras.containsKey("regionUrl")) {
            Utils.getRegionByUrlName(extras.getString("regionUrl"), callback);
        }
    }

    private void showPeriod(Period period) {
        int levelIndex = period.getCovid_automat() - 1;
        int color = Color.parseColor(CovidAutomat.colors[levelIndex]);
        setColors(color);

        ((TextView) findViewById(R.id.level_text)).setText(String.format(
            Locale.getDefault(),
            "Skóre %d: %s",
            period.getCovid_automat(),
            CovidAutomat.levels[levelIndex]
        ));

        ((TextView) findViewById(R.id.date)).setText(String.format(
            Locale.getDefault(),
            "Platné od %s do %s",
            dateFormat.format(period.getValidFrom()),
            dateFormat.format(period.getValidTo())
        ));

        showActions(Arrays.asList(period.getAutomaticActions()));
    }

    private void setColors(int color) {
        getWindow().setStatusBarColor(color);
        findViewById(R.id.app_bar).setBackgroundColor(color);

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setBackgroundColor(color);
        toolbarLayout.setContentScrimColor(color);
        toolbarLayout.setStatusBarScrimColor(color);
    }

    private void fabAction(View v) {
        if (currentPeriod)
            showPeriod(region.getNextPeriod());
        else
            showPeriod(region.getCurrentPeriod());

        String text = currentPeriod ? "budúci" : "tento";
        Toast.makeText(this, String.format("Zobrazujem %s týždeň", text), Toast.LENGTH_SHORT).show();

        currentPeriod = !currentPeriod;
    }

    private void showActions(List<Action> actions) {
        ((RecyclerView) findViewById(R.id.categories_holder))
            .setAdapter(new RegionInfoAdapter(actions));
    }


}
