package com.memonade.apptracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

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

        View aboutPage = null;
        try {
            aboutPage = new AboutPage(this)
                    .isRTL(false)
                    .setImage(R.mipmap.apptracker_icon_round)
                    .setDescription(getString(R.string.app_description))
                    .addItem(new Element().setTitle("Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName))
                    .addItem(new Element().setTitle(getString(R.string.developer_name)))
                    .addEmail(getString(R.string.email_address), "Feedback")
                    .addPlayStore(getPackageName())
                    .addItem(new Element()
                            .setTitle(getString(R.string.licences_title))
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), LicencesActivity.class);
                                    startActivity(intent);
                                }
                            }))
                    .create();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setContentView(aboutPage);
    }

}
