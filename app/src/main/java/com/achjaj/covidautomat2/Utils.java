package com.achjaj.covidautomat2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.achjaj.covidautomat2.ui.regioninfo.RegionInfoActivity;
import org.achjaj.covid.CovidAutomat;
import org.achjaj.covid.Region;

import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Utils {
    public static final CovidAutomat covidAutomat = new CovidAutomat("com.achjaj.covidautomat:0.1 (Android app)");
    private static final Pattern DIACRITICS_AND_FRIENDS
        = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    public static void getRegionByCode(int code, ICallback callback) {
        Thread thread = new Thread(() -> {
            try {
                callback.call(covidAutomat.getRegionByCode(code), null);
            } catch (IOException e) {
                callback.call(null, e);
            }
        });
        thread.start();
    }

    public static void getRegionByName(String name, ICallback callback) {
        getRegionByCode(covidAutomat.getRegions().get(name), callback);
    }

    public static void getRegionByUrlName(String urlName, ICallback callback) {
        int code = translateUrlName(urlName);
        getRegionByCode(code, callback);
    }

    public static void showRegionInfo(String bKey, String regionId, Activity activity, boolean nextWeek) {
        Intent intent = new Intent(activity, RegionInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(bKey, regionId);
        bundle.putBoolean("nextWeek", nextWeek);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static int translateUrlName(String urlName) {
        HashMap<String, Integer> regions = covidAutomat.getRegions();

        if (urlName.equals("bratislava"))
            return regions.get("Bratislava I");
        else if (urlName.equals("kosice-okolie"))
            return regions.get("Košice - okolie");
        else if (urlName.equals("kosice"))
            return regions.get("Košice I");

        for (String key : regions.keySet()) {
            String name = stripDiacritics(
                key.toLowerCase().replaceAll(" ", "-")
            );

            if (name.equals(urlName))
                return regions.get(key);
        }

        return -1;
    }

    public static String stripDiacritics(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
        return str;
    }

    public interface ICallback {
        void call(Region region, IOException e);
    }

    public static void showExceptionDialog(String msg, Exception e, Context context, DialogInterface.OnClickListener onClose) {
        new AlertDialog.Builder(context)
            .setTitle(R.string.error_title)
            .setMessage(msg)
            .setPositiveButton(R.string.close, onClose)
            .setNeutralButton(R.string.report, (dialog, which) -> {/*TODO*/})
            .setOnDismissListener(dialog -> onClose.onClick(null, -1))
            .show();
    }

    public static void showLinkClickableTextDialog(Context context, String text) {
        ScrollView scrollView = new ScrollView(context);

        TextView msgView = new TextView(context, null, R.style.TextAppearance_AppCompat_Large);
        msgView.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        msgView.setMovementMethod(LinkMovementMethod.getInstance());

        int dp =toPx(10, context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp, dp, dp, dp);
        msgView.setLayoutParams(params);

        scrollView.addView(msgView);
        new androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(scrollView)
            .setNeutralButton(R.string.close, (dialog, which) -> dialog.dismiss())
            .show();
    }

    public static int toPx(int dp, Context context) {
        Resources r = context.getResources();

        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.getDisplayMetrics()
        );
    }
}
