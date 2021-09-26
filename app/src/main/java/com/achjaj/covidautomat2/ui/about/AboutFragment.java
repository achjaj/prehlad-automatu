package com.achjaj.covidautomat2.ui.about;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.achjaj.covidautomat2.BuildConfig;
import com.achjaj.covidautomat2.R;
import com.achjaj.covidautomat2.Utils;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

import java.util.Arrays;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        AboutPage page = new AboutPage(getContext())
            .isRTL(false)
            .setImage(R.drawable.ic_main_icon)
            .setDescription(getDescription())
            .addItem(textElement("Verzia: " + BuildConfig.VERSION_NAME))
            .addItem(licenseElement("Licencia", getMITLicense(R.string.copyright_app)))
            .addGroup(getString(R.string.contacts))
            .addEmail(getString(R.string.email), getString(R.string.email_title))
            .addGitHub(getString(R.string.github), getString(R.string.github_title))
            .addGroup(getString(R.string.lib_lecenses_title));

        Arrays.stream(new Pair[] {
            new Pair("Jsoup", getMITLicense(R.string.copyright_jsoup)),
            new Pair("Jackson © FasterXML, LLC", getString(R.string.license_jackson)),
            new Pair("Android About Page", getMITLicense(R.string.copyright_about_page))
        }).forEach(pair -> page.addItem(licenseElement((String) pair.first, (String) pair.second)));

        View view = page.create();
        ((TextView) view.findViewById(R.id.description)).setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    private Spanned getDescription() {
        return Html.fromHtml(
            getString(R.string.about_text),
            Html.FROM_HTML_MODE_LEGACY
        );
    }

    private Element textElement(String text) {
        Element element = new Element();
        element.setTitle(text);

        return element;
    }

    private Element licenseElement(String label, String license) {
        Element element = new Element();
        element.setTitle(label);
        element.setValue("VALUE");
        element.setOnClickListener(v -> {
            Context context = v.getContext();
            Utils.showLinkClickableTextDialog(context, license);
        });

        return element;
    }

    private String getMITLicense(int copyrightId) {
        return getString(R.string.mit_license).replace("#copyright", getString(copyrightId));
    }
}
