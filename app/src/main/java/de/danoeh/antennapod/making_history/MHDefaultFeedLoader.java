package de.danoeh.antennapod.making_history;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.net.download.serviceinterface.FeedUpdateManager;
import de.danoeh.antennapod.storage.database.FeedDatabaseWriter;
import de.danoeh.antennapod.storage.importexport.OpmlElement;
import de.danoeh.antennapod.storage.importexport.OpmlReader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MHDefaultFeedLoader {
    private final static String TAG = "MHDefaultFeedLoader";
    private final static String OPML_FEED_URL = "https://firebasestorage.googleapis.com/v0/b/makinghistory-1579519443087.appspot.com/o/default_opml.xml?alt=media";
    private final static String SHARED_PREFERENCES_NAME = "MH_SHARED_PREFERENCES";
    private final static String OPML_HASH_PREF_NAME = "OPML_HASH";
    private final static String UNWANTED_FEEDS_LIST_PREF_NAME = "UNWANTED_FEEDS_LIST";

    public static void loadDefaultOPMLIfNeeded(final Activity activity) {
        Request request = new Request.Builder().url(OPML_FEED_URL).build();
        OkHttpClient client = new OkHttpClient.Builder().build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull java.io.IOException e) {
                Log.d(TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                int stringHash = 0;
                SharedPreferences prefs = activity.getSharedPreferences(
                        SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

                try {
                    assert response.body() != null;
                    String opmlData = response.body().string();
                    stringHash = opmlData.hashCode();

                    if (prefs.getInt(OPML_HASH_PREF_NAME, 0) == stringHash) {
                        // No change
                        return;
                    }

                    Set<String> unwantedPodcasts = prefs.getStringSet(UNWANTED_FEEDS_LIST_PREF_NAME, Collections.emptySet());

                    final InputStream opmlStream = new ByteArrayInputStream(
                            opmlData.getBytes(StandardCharsets.UTF_8));

                    activity.runOnUiThread(() -> {
                        try {
                            Reader reader = new InputStreamReader(opmlStream, StandardCharsets.UTF_8);
                            OpmlReader opmlReader = new OpmlReader();
                            ArrayList<OpmlElement> elements = opmlReader.readDocument(reader);

                            for (OpmlElement element : elements) {

                                Feed feed = new Feed(
                                        element.getXmlUrl(),
                                        null,
                                        element.getText() != null ? element.getText() : "Unknown podcast"
                                );
                                feed.setItems(Collections.emptyList());

                                // Save feed in DB
                                if (!unwantedPodcasts.contains(element.getXmlUrl())) {
                                    FeedDatabaseWriter.updateFeed(activity, feed, false);

                                    MHDefaultFeedLoader.addUnwantedFeedToList(activity, element.getXmlUrl());
                                }
                            }

                            // Finally, trigger a feed update for all new feeds
                            FeedUpdateManager.getInstance().runOnce(activity);

                        } catch (Exception e) {
                            Log.e(TAG, Log.getStackTraceString(e));
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                // Save new hash
                prefs.edit().putInt(OPML_HASH_PREF_NAME, stringHash).apply();
            }
        });
    }

    /***
     * Adds a podcast to the list of unwanted podcasts, this happends when the user decides to remove
     * a podcast from the list, and will make the automatic podcast downloader ignore this podcast
     * in later loadings.
     */
    public static void addUnwantedFeedToList(Context context, String feedURL)
    {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS);
        Set<String> unwantedPodcasts = prefs.getStringSet(UNWANTED_FEEDS_LIST_PREF_NAME, new HashSet<String>());
        Set<String> newUnwantedPodcasts = new HashSet<String>(unwantedPodcasts);
        newUnwantedPodcasts.add(feedURL);
        prefs.edit().putStringSet(UNWANTED_FEEDS_LIST_PREF_NAME, newUnwantedPodcasts).apply();
    }
}
