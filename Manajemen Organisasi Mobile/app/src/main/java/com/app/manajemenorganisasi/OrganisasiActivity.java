package com.app.manajemenorganisasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OrganisasiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_organisasi);
    }

    public void dpm(View view) {
        Intent intent = new Intent(OrganisasiActivity.this, UserActivity.class);
        intent.putExtra("group", "dpm");
        startActivity(intent);
    }

    public void bem(View view) {
        Intent intent = new Intent(OrganisasiActivity.this, UserActivity.class);
        intent.putExtra("group", "bem");
        startActivity(intent);
    }

    public void hms(View view) {
        Intent intent = new Intent(OrganisasiActivity.this, UserActivity.class);
        intent.putExtra("group", "hms");
        startActivity(intent);
    }

    public void hmm(View view) {
        Intent intent = new Intent(OrganisasiActivity.this, UserActivity.class);
        intent.putExtra("group", "hmm");
        startActivity(intent);
    }

    public void hme(View view) {
        Intent intent = new Intent(OrganisasiActivity.this, UserActivity.class);
        intent.putExtra("group", "hme");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OrganisasiActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}