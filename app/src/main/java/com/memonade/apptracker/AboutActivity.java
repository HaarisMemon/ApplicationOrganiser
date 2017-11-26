package com.memonade.apptracker;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * This class represents the activity which displays the information about the app.
 * @author HaarisMemon
 */
public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setTitle(getString(R.string.about));

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        final AboutActivity aboutActivity = this;

        View aboutPage = null;
        try {
            aboutPage = new AboutPage(this)
                    .isRTL(false)
                    .setImage(R.mipmap.apptracker_icon_round)
                    .setDescription(getString(R.string.app_description))
                    .addItem(new Element().setTitle("Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName))
                    .addItem(new Element().setTitle(getString(R.string.developer_name)))
                    .addEmail(getString(R.string.email_address), getString(R.string.feedback))
                    .addPlayStore(getPackageName(), "Rate this app on the Play Store")
                    .addItem(new Element()
                            .setTitle(getString(R.string.licences_title))
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    WebView webView = (WebView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_licences, null);
                                    webView.loadUrl("file:///android_asset/open_source_licences.html");
                                    new AlertDialog.Builder(aboutActivity, R.style.Theme_AppCompat_Light_Dialog_Alert)
                                            .setTitle(getString(R.string.licences_title))
                                            .setView(webView)
                                            .setPositiveButton(android.R.string.ok, null)
                                            .show();
                                }
                            }))
                    .create();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setContentView(aboutPage);
    }

}
