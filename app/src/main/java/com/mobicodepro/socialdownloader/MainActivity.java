package com.mobicodepro.socialdownloader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.GoogleApiClient;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import config.admob;
import func.movefile;
import func.notifications;

public class MainActivity extends AppCompatActivity {

    private int mCounte = 0;
    private InterstitialAd interstitial;
    boolean doubleBackToExitPressedOnce = false;
    public static String filepath = "";

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /**
         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
        }
        });
         **/


        SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.pref_appname), Context.MODE_PRIVATE);

        if (sharedPref.getBoolean("isFistTime", true)) {

            String folderName = getResources().getString(R.string.foldername);

            String mBaseFolderPath = Environment
                    .getExternalStorageDirectory()
                    + File.separator
                    + folderName + File.separator;
            if (!new File(mBaseFolderPath).exists()) {
                new File(mBaseFolderPath).mkdir();
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("isFistTime", false);
            editor.putBoolean(getResources().getString(R.string.pref_notification), true);
            editor.putBoolean(getResources().getString(R.string.pref_hidenotification), true);
            editor.putString("path", mBaseFolderPath);
            editor.commit();

        }


        ArrayList<String> tabs = new ArrayList<>();
        tabs.add(getResources().getString(R.string.tab_home));
        tabs.add(getResources().getString(R.string.tab_download));
        tabs.add(getResources().getString(R.string.tab_guide));


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                tabs));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 1) {

                    recyclerview j = ((recyclerview) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.viewpager + ":1"));
                    j.loadMedia();

                    mCounte++;

                    String mCounter = getResources().getString(R.string.admob_interstitial_counter);
                    // display interstitial
                    if (mCounte == Integer.parseInt(mCounter)) {

                        displayInterstitial();

                        mCounte = 0;
                    }

                }
                if (tab.getPosition() == 2) {

                    facebook e = ((facebook) getSupportFragmentManager()
                            .findFragmentByTag("android:switcher:" + R.id.viewpager + ":2"));
                    e.loadWebView();

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // Notification
        if (sharedPref.getBoolean(getResources().getString(R.string.pref_notification), true)) {
            notifications.notify(getResources().getString(R.string.app_name), "Click here to start download video!", R.mipmap.ic_launcher, this, MainActivity.class);
        }

        // prepare interstitial
        if (getResources().getString(R.string.onoff_Interstitial).toLowerCase(Locale.ENGLISH).equals("on")) {

            requestInterstitial();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //now getIntent() should always return the last received intent
    }

    private void requestInterstitial() {

        if (getResources().getString(R.string.onoff_Interstitial).toLowerCase(Locale.ENGLISH).equals("on")) {

            // Create the interstitial.
            interstitial = new InterstitialAd(this);
            interstitial.setAdUnitId(admob.Interstitial);

            // Create ad request.
            AdRequest adRequest = new AdRequest.Builder().build();

            // Begin loading your interstitial.
            interstitial.loadAd(adRequest);

            // Set an AdListener.
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Proceed to the next level.
                    requestInterstitial();
                }
            });
        }
    }


    public void displayInterstitial() {
        if (getResources().getString(R.string.onoff_Interstitial).toLowerCase(Locale.ENGLISH).equals("on")) {

            if (interstitial.isLoaded()) {
                interstitial.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {

        // double click to exit
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 34) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {

                if (!filepath.isEmpty()) {
                    File src = new File(filepath);
                    File destination = new File(data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));

                    try {

                        movefile.mf(src, destination);

                        recyclerview j = ((recyclerview) getSupportFragmentManager()
                                .findFragmentByTag("android:switcher:" + R.id.viewpager + ":1"));
                        j.loadMedia();

                        Toast.makeText(this, "Moved Successful.", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {

                        Toast.makeText(getApplicationContext(), "Sorry we can't move file. try Other file!", Toast.LENGTH_LONG).show();

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Sorry we can't move file. try Other file!", Toast.LENGTH_LONG).show();
                }

            } else {
                // Nothing selected
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
