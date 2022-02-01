package com.app.manajemenorganisasi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    private TextView tv_username, tv_desc;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        tv_username = findViewById(R.id.tv_account_username);
        tv_desc = findViewById(R.id.tv_account_desc);

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        tv_username.setText("@" + sharedPreferences.getString("username", "username"));
        String desc = String.format(
                "%s %s FT UNISMA",
                sharedPreferences.getString("role_name", "role_name").toUpperCase(),
                sharedPreferences.getString("role_group", "role_group").toUpperCase()
                );
        tv_desc.setText(desc);
    }

    public void logout(View view) {
        setResult(RESULT_OK);
        finish();
    }
}