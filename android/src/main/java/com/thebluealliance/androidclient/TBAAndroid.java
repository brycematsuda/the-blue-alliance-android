package com.thebluealliance.androidclient;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * File created by phil on 7/21/14.
 */
public class TBAAndroid extends Application {

    private static final String PROD_ANALYTICS_KEY = "analytics.id";
    private static final String DEBUG_ANALYTICS_KEY = "analytics.id.debug";

    public enum GAnalyticsTracker{
        ANDROID_TRACKER;    // main tracker. We can add others in the future, if we need
    }

    static HashMap<GAnalyticsTracker, Tracker> mTrackers = new HashMap<>();

    private static GoogleAnalytics analytics;

    public Tracker getTracker(GAnalyticsTracker tracker){
        return getTracker(tracker, this);
    }

    public static synchronized Tracker getTracker(GAnalyticsTracker trackerId, Context c) {
        if (!mTrackers.containsKey(trackerId)) {
            if(analytics == null) {
                analytics = GoogleAnalytics.getInstance(c);
                boolean dryRun;

                if (Utilities.isDebuggable()) {
                    dryRun = PreferenceManager.getDefaultSharedPreferences(c).getBoolean("analytics_dry_run", true);
                } else {
                    dryRun = false;
                }
                analytics.setDryRun(dryRun);
                Log.d("GAV4", "Setting analytics dry run? " + dryRun);
            }

            String id;
            if(Utilities.isDebuggable()){
                boolean useDebugKey = PreferenceManager.getDefaultSharedPreferences(c).getBoolean("analytics_debug_key", true);
                id = Utilities.readLocalProperty(c, useDebugKey ? DEBUG_ANALYTICS_KEY : PROD_ANALYTICS_KEY);
            }else{
                id = Utilities.readLocalProperty(c, PROD_ANALYTICS_KEY);
            }
            Tracker t;
            Log.d("GAV4", "Loaded analytics id: "+id);
            t = analytics.newTracker(id);
            t.enableAutoActivityTracking(true);
            t.enableExceptionReporting(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public void setAnalyticsDryRun(boolean dryRun){
        if(analytics == null) {
            analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(dryRun);
            Log.d("GAV4", "Setting analytics dry run? " + dryRun);
        }
    }
}
