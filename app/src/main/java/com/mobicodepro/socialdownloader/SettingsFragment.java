package com.mobicodepro.socialdownloader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.File;

/**
 * Created by mac on 26/02/16.
 */
public class SettingsFragment extends PreferenceFragment {
    int REQUEST_DIRECTORY = 198;
    Preference rateus , sharethisapp , moreapp , path , contactme;
    private String mBaseFolderPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

        rateus = (Preference) findPreference("rateus");
        sharethisapp = (Preference) findPreference("sharethisapp");
        moreapp = (Preference) findPreference("moreapp");
        path = (Preference) findPreference("path");
        contactme = (Preference) findPreference("contactme");

        SharedPreferences preferences = getActivity().getSharedPreferences(getResources().getString(R.string.pref_appname), Context.MODE_PRIVATE);

        String folderName = getResources().getString(R.string.foldername);

            if(!preferences.getString("path", "DEFAULT").equals("DEFAULT")){

                mBaseFolderPath = preferences
                        .getString("path", "DEFAULT");

            }else{

                mBaseFolderPath = android.os.Environment
                        .getExternalStorageDirectory()
                        + File.separator
                        + folderName + File.separator;
            }

            path.setSummary(mBaseFolderPath);

        SwitchPreference notification = (SwitchPreference)findPreference(getResources().getString(R.string.pref_notification));
        SwitchPreference disableNotificationsDownloader = (SwitchPreference)findPreference(getResources().getString(R.string.pref_hidenotification));

        notification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object isdataTrafficEnabled) {

                boolean isDataTrafficOn = ((Boolean) isdataTrafficEnabled).booleanValue();

                SharedPreferences sharedpref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_appname), Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sharedpref.edit();
                e.putBoolean(getResources().getString(R.string.pref_notification), isDataTrafficOn);
                e.commit();

                return true;
            }
        });

        disableNotificationsDownloader.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object isdataTrafficEnabled) {

                boolean isDataTrafficOn = ((Boolean) isdataTrafficEnabled).booleanValue();

                SharedPreferences sharedpref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_appname), Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sharedpref.edit();
                e.putBoolean(getResources().getString(R.string.pref_hidenotification), isDataTrafficOn);
                e.commit();

                return true;
            }
        });

        path.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                final Intent chooserIntent = new Intent(
                        getActivity(),
                        DirectoryChooserActivity.class);

                final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                        .newDirectoryName(getResources().getString(R.string.foldername))
                        .allowReadOnlyDirectory(true)
                        .allowNewDirectoryNameModification(true)
                        .build();

                chooserIntent.putExtra(
                        DirectoryChooserActivity.EXTRA_CONFIG,
                        config);

                startActivityForResult(chooserIntent, REQUEST_DIRECTORY);

                return true;
            }
        });

        rateus.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }

                return true;
            }
        });

        sharethisapp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                func.share.mShareText("Hey my friend check out this app\n https://play.google.com/store/apps/details?id="+ getActivity().getPackageName() + " \n", getActivity());
                return true;

            }
        });

        moreapp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Stormania")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=pub:Stormania")));
                }
                return true;

            }
        });

        contactme.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:mobicodepro@gmail.com"+
                            "?subject=" + Uri.encode(getActivity().getPackageName())));
                    startActivity(intent);
                }catch(Exception e){
                }
                return true;

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY) {
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {

                SharedPreferences sharedpref = getActivity().getSharedPreferences(getResources().getString(R.string.pref_appname), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpref.edit();
                editor.putString("path", data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));
                editor.commit();
                path.setSummary(data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR));

                // using this method to update my recycler
//                Intent jp = new Intent(getActivity() , MainActivity.class);
//                startActivityForResult(jp, 2901);

            } else {
                // Nothing selected
            }
        }
    }

}