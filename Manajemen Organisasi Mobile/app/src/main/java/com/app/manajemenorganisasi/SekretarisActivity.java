package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SekretarisActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_FILE1 = 8000, REQUEST_PICK_FILE2 = 8001;
    private static final int REQUEST_LOGOUT = 5001;
    private Uri filePath1, filePath2;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef_surat_masuk, mRef_surat_keluar;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ProgressDialog pd;

    private LinearLayout ll1, ll2;
    private EditText et_judul1, et_nomor1, et_judul2, et_nomor2;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sekertaris);

        ll1 = findViewById(R.id.layout1);
        ll2 = findViewById(R.id.layout2);

        et_judul1 = findViewById(R.id.et_judul_surat1);
        et_judul2 = findViewById(R.id.et_judul_surat2);
        et_nomor1 = findViewById(R.id.et_nomor_surat1);
        et_nomor2 = findViewById(R.id.et_nomor_surat2);

        pd                  = new ProgressDialog(this);
        pd.setMessage("Memproses..");

        firebaseStorage     = FirebaseStorage.getInstance();
        storageReference    = firebaseStorage.getReference();
        mDatabase               = FirebaseDatabase.getInstance();

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group = sharedPreferences.getString("role_group", "");
            mRef_surat_masuk        = mDatabase.getReference(group + "/surat_masuk");
            mRef_surat_keluar       = mDatabase.getReference(group + "/surat_keluar");
        }else{
            startActivity(new Intent(SekretarisActivity.this, MainActivity.class));
            finish();
        }
    }

    public void surat(View view) {
        startActivity(new Intent(SekretarisActivity.this, SuratActivity.class));
    }

    public void addFile1(View view) {
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Pilih File"), REQUEST_PICK_FILE1);
    }

    public void addFile2(View view) {
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Pilih File"), REQUEST_PICK_FILE2);
    }

    public void upload1(View view) {
        String judul = et_judul1.getText().toString();
        String nomor = et_nomor1.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        String date = sdf.format(new Date());

        if(!judul.isEmpty() && !nomor.isEmpty() && filePath1 != null){
            pd.show();

            String filename = UUID.randomUUID().toString();

            Map<String, String> data = new HashMap<>();
            data.put("judul", judul);
            data.put("nomor", nomor);
            data.put("lokasi_surat", filename + ".pdf");

            StorageReference ref = storageReference.child("documents/" + filename + ".pdf" );
            ref.putFile(filePath1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mRef_surat_masuk.child(date).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    et_judul1.setText("");
                                    et_nomor1.setText("");
                                    filePath1 = null;
                                    ll1.setBackgroundColor(Color.WHITE);
                                    Toast.makeText(SekretarisActivity.this, "Berhasil Upload", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(SekretarisActivity.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(SekretarisActivity.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    public void upload2(View view) {
        String judul = et_judul2.getText().toString();
        String nomor = et_nomor2.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        String date = sdf.format(new Date());

        if(!judul.isEmpty() && !nomor.isEmpty() && filePath2 != null){
            pd.show();

            String filename = UUID.randomUUID().toString();

            Map<String, String> data = new HashMap<>();
            data.put("judul", judul);
            data.put("nomor", nomor);
            data.put("lokasi_surat", filename + ".pdf");

            StorageReference ref = storageReference.child("documents/" + filename + ".pdf" );
            ref.putFile(filePath2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mRef_surat_keluar.child(date).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    et_judul2.setText("");
                                    et_nomor2.setText("");
                                    filePath2 = null;
                                    ll2.setBackgroundColor(Color.WHITE);
                                    Toast.makeText(SekretarisActivity.this, "Berhasil Upload", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(SekretarisActivity.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }else{
                        Toast.makeText(SekretarisActivity.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PICK_FILE1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath1 = data.getData();
            ll1.setBackgroundColor(Color.GREEN);
        }else if(requestCode == REQUEST_PICK_FILE2 && resultCode == RESULT_OK && data != null && data.getData() != null){
            ll2.setBackgroundColor(Color.GREEN);
            filePath2 = data.getData();
        }else if(requestCode == REQUEST_LOGOUT && resultCode == RESULT_OK){
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(SekretarisActivity.this, MainActivity.class));
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
                startActivityForResult(new Intent(SekretarisActivity.this, AccountActivity.class), REQUEST_LOGOUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}