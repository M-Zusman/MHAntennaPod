package de.danoeh.antennapod.ui.screen.preferences;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import com.bytehamster.lib.preferencesearch.SearchConfiguration;
import com.bytehamster.lib.preferencesearch.SearchPreference;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.ui.common.IntentUtils;
import de.danoeh.antennapod.ui.preferences.screen.AnimatedPreferenceFragment;
import de.danoeh.antennapod.ui.preferences.screen.about.AboutFragment;

public class MainPreferencesFragment extends AnimatedPreferenceFragment {

    private static final String PREF_SCREEN_USER_INTERFACE = "prefScreenInterface";
    private static final String PREF_SCREEN_PLAYBACK = "prefScreenPlayback";
    private static final String PREF_SCREEN_DOWNLOADS = "prefScreenDownloads";
    private static final String PREF_SCREEN_IMPORT_EXPORT = "prefScreenImportExport";
    private static final String PREF_SCREEN_SYNCHRONIZATION = "prefScreenSynchronization";
    private static final String PREF_DOCUMENTATION = "prefDocumentation";
    private static final String PREF_VIEW_FORUM = "prefViewForum";
    private static final String PREF_SEND_BUG_REPORT = "prefSendBugReport";
    private static final String PREF_CATEGORY_PROJECT = "project";
    private static final String PREF_ABOUT = "prefAbout";
    private static final String PREF_NOTIFICATION = "notifications";
    private static final String PREF_CONTRIBUTE = "prefContribute";

    //MH-FEATURE: Source code url.
    private static final String SOURCE_CODE_URL = "https://github.com/M-Zusman/MHAntennaPod/tree/mh_develop";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        setupMainScreen();
        setupSearch();

        // If you are writing a spin-off, please update the details on screens like "About" and "Report bug"
        // and afterwards remove the following lines. Please keep in mind that AntennaPod is licensed under the GPL.
        // This means that your application needs to be open-source under the GPL, too.
        // It must also include a prominent copyright notice.

        //MH-FEATURE: Add copyright dialog
        findPreference("copyrightNotice").setOnPreferenceClickListener(preference -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.notice)
                    .setMessage(R.string.copyright_notice)
                    .setNegativeButton(R.string.visit, (dialogInterface, i) ->
                            IntentUtils.openInBrowser(getContext(), SOURCE_CODE_URL))
                    .setPositiveButton(R.string.close_label, null)
                    .show();

            return true;
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        ((PreferenceActivity) getActivity()).getSupportActionBar().setTitle(R.string.settings_label);
    }

    private void setupMainScreen() {
        findPreference(PREF_SCREEN_USER_INTERFACE).setOnPreferenceClickListener(preference -> {
            ((PreferenceActivity) getActivity()).openScreen(R.xml.preferences_user_interface);
            return true;
        });
        findPreference(PREF_SCREEN_PLAYBACK).setOnPreferenceClickListener(preference -> {
            ((PreferenceActivity) getActivity()).openScreen(R.xml.preferences_playback);
            return true;
        });
        findPreference(PREF_SCREEN_DOWNLOADS).setOnPreferenceClickListener(preference -> {
            ((PreferenceActivity) getActivity()).openScreen(R.xml.preferences_downloads);
            return true;
        });
        findPreference(PREF_SCREEN_SYNCHRONIZATION).setOnPreferenceClickListener(preference -> {
            ((PreferenceActivity) getActivity()).openScreen(R.xml.preferences_synchronization);
            return true;
        });
        findPreference(PREF_SCREEN_IMPORT_EXPORT).setOnPreferenceClickListener(preference -> {
            ((PreferenceActivity) getActivity()).openScreen(R.xml.preferences_import_export);
            return true;
        });
        findPreference(PREF_NOTIFICATION).setOnPreferenceClickListener(preference -> {
            ((PreferenceActivity) getActivity()).openScreen(R.xml.preferences_notifications);
            return true;
        });
        findPreference(PREF_ABOUT).setOnPreferenceClickListener(
                preference -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.settingsContainer, new AboutFragment())
                            .addToBackStack(getString(R.string.about_pref)).commit();
                    return true;
                }
        );
        findPreference(PREF_SEND_BUG_REPORT).setOnPreferenceClickListener(preference -> {
            //MH-FEATURE: Open the source code in browser.
            IntentUtils.openInBrowser(getContext(), SOURCE_CODE_URL);
            return true;
        });
    }

    private void setupSearch() {
        SearchPreference searchPreference = findPreference("searchPreference");
        SearchConfiguration config = searchPreference.getSearchConfiguration();
        config.setActivity((AppCompatActivity) getActivity());
        config.setFragmentContainerViewId(R.id.settingsContainer);
        config.setBreadcrumbsEnabled(true);

        config.index(R.xml.preferences_user_interface)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_user_interface));
        config.index(R.xml.preferences_playback)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_playback));
        config.index(R.xml.preferences_downloads)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_downloads));
        config.index(R.xml.preferences_import_export)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_import_export));
        config.index(R.xml.preferences_autodownload)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_downloads))
                .addBreadcrumb(R.string.automation)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_autodownload));
        config.index(R.xml.preferences_synchronization)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_synchronization));
        config.index(R.xml.preferences_notifications)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_notifications));
        config.index(R.xml.feed_settings)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.feed_settings));
        config.index(R.xml.preferences_swipe)
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_user_interface))
                .addBreadcrumb(PreferenceActivity.getTitleOfPage(R.xml.preferences_swipe));
    }
}
