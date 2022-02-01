package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BendaharaActivity extends AppCompatActivity {

    private static final int REQUEST_LOGOUT = 5001;
    private EditText et_keterangan1, et_jumlah1, et_keterangan2, et_jumlah2;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;
    private ProgressDialog pd;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bendahara1);

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses..");

        et_keterangan1 = findViewById(R.id.et_keterangan_uang1);
        et_keterangan2 = findViewById(R.id.et_keterangan_uang2);

        et_jumlah1 = findViewById(R.id.et_jumlah_uang1);
        et_jumlah2 = findViewById(R.id.et_jumlah_uang2);

        firebaseDatabase = FirebaseDatabase.getInstance();

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group = sharedPreferences.getString("role_group", "");
            mRef = firebaseDatabase.getReference(group);
        }else{
            startActivity(new Intent(BendaharaActivity.this, MainActivity.class));
            finish();
        }
    }

    public void datakeuangan(View view) {
        startActivity(new Intent(BendaharaActivity.this, KeuanganActivity.class));
    }

    public void uploadUang1(View view) {
        String keterangan, jumlah;
        keterangan = et_keterangan1.getText().toString();
        jumlah = et_jumlah1.getText().toString();

        if(!keterangan.isEmpty() && !jumlah.isEmpty()){
            pd.show();
            Map<String, String> data = new HashMap<>();
            data.put("keterangan", keterangan);
            data.put("jumlah", jumlah);

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
            String date = sdf.format(new Date());
            mRef.child("uang_masuk").child(date).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.cancel();
                    if(task.isSuccessful()){
                        et_keterangan1.setText("");
                        et_jumlah1.setText("");
                        Toast.makeText(BendaharaActivity.this, "Berhasil Upload", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(BendaharaActivity.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadUang2(View view) {
        String keterangan, jumlah;
        keterangan = et_keterangan2.getText().toString();
        jumlah = et_jumlah2.getText().toString();

        if(!keterangan.isEmpty() && !jumlah.isEmpty()){
            pd.show();
            Map<String, String> data = new HashMap<>();
            data.put("keterangan", keterangan);
            data.put("jumlah", jumlah);

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
            String date = sdf.format(new Date());
            mRef.child("uang_keluar").child(date).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.cancel();
                    if(task.isSuccessful()){
                        et_keterangan2.setText("");
                        et_jumlah2.setText("");
                        Toast.makeText(BendaharaActivity.this, "Berhasil Upload", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(BendaharaActivity.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.account:
                startActivityForResult(new Intent(BendaharaActivity.this, AccountActivity.class), REQUEST_LOGOUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_LOGOUT && resultCode == RESULT_OK){
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(BendaharaActivity.this, MainActivity.class));
        }
    }
}