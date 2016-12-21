package com.mobicodepro.socialdownloader;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by mac on 26/02/16.
 */
public class SettingsActivity extends PreferenceActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingsFragment()).commit();

    }
}
