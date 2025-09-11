package de.danoeh.antennapod.ui.preferences.screen.about;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import de.danoeh.antennapod.ui.common.IntentUtils;
import de.danoeh.antennapod.ui.preferences.BuildConfig;
import de.danoeh.antennapod.ui.preferences.R;
import de.danoeh.antennapod.ui.preferences.screen.AnimatedPreferenceFragment;

public class AboutFragment extends AnimatedPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_about);

        //MH-FEATURE: Remove irrelevant preferences
        findPreference("about_licenses").setOnPreferenceClickListener((preference) -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.settingsContainer, new LicensesFragment())
                    .addToBackStack(getString(R.string.translators)).commit();
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.about_pref);
    }
}
