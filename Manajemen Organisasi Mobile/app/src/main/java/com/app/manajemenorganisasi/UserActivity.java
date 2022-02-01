package com.app.manajemenorganisasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class UserActivity extends AppCompatActivity {

    private String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemhs);

        if(getIntent().hasExtra("group")){
            group = getIntent().getStringExtra("group");
        }else{
            finish();
        }
    }

    public void beritakegiatan(View view) {
        Intent intent = new Intent(UserActivity.this, ActivityDaftarBerita.class);
        intent.putExtra("group", group);
        startActivity(intent);
    }

    public void pengertianomawa(View view)
    {
        Intent intent = new Intent(UserActivity.this, OrmawaActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
    }

    public void jadwalomawa(View view) {
        Intent intent = new Intent(UserActivity.this, ListJadwalActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
    }

    public void minat(View view) {
        Intent intent = new Intent(UserActivity.this, MinatNBakatActivity.class);
        intent.putExtra("group", group);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}