package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KRTActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST1 = 7000;
    private static final int PICK_IMAGE_REQUEST2 = 7001;
    private static final int REQUEST_LOGOUT = 5001;

    private EditText et_nama1, et_nama2, et_keterangan1, et_keterangan2;
    private Uri filePath1, filePath2;
    private ImageView iv_preview1, iv_preview2;

    private SharedPreferences sharedPreferences;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef1, mRef2;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kerumahtanggaan);

        et_nama1        = findViewById(R.id.et_nama_barang1);
        et_nama2        = findViewById(R.id.et_nama_barang2);
        et_keterangan1     = findViewById(R.id.et_keterangan_barang1);
        et_keterangan2     = findViewById(R.id.et_keterangan_barang2);

        iv_preview1     = findViewById(R.id.iv_preview_barang1);
        iv_preview2     = findViewById(R.id.iv_preview_barang2);

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses..");

        firebaseDatabase = FirebaseDatabase.getInstance();

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group    = sharedPreferences.getString("role_group", "");
            mRef1           = firebaseDatabase.getReference(group + "/barang_masuk");
            mRef2           = firebaseDatabase.getReference(group + "/barang_keluar");
        }else{
            startActivity(new Intent(KRTActivity.this, MainActivity.class));
            finish();
        }

        firebaseStorage     = FirebaseStorage.getInstance();
        storageReference    = firebaseStorage.getReference();
    }

    public void lihatBarang(View view) {
        startActivity(new Intent(KRTActivity.this, BarangActivity.class));
    }

    public void tambahGambarBarang1(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Pilih Gambar"), PICK_IMAGE_REQUEST1);
    }

    public void tambahGambarBarang2(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Pilih Gambar"), PICK_IMAGE_REQUEST2);
    }


    public void uploadBarang1(View view) {
        String nama         = et_nama1.getText().toString();
        String keterangan   = et_keterangan1.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        String date = sdf.format(new Date());
        String filename = UUID.randomUUID().toString();

        Map<String, String> data = new HashMap<>();
        data.put("nama", nama);
        data.put("keterangan", keterangan);
        data.put("lokasi_gambar", filename);

        if(!nama.isEmpty() && !keterangan.isEmpty() && filePath1 != null){
            pd.show();

            StorageReference ref = storageReference.child("images/" + filename);
            ref.putFile(filePath1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mRef1.child(date).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    et_nama1.setText("");
                                    et_keterangan1.setText("");
                                    iv_preview1.setImageBitmap(null);
                                    iv_preview1.setBackground(getResources().getDrawable(R.drawable.ic_baseline_image_360));
                                    Toast.makeText(KRTActivity.this, "Berhasil mengupload!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(KRTActivity.this, "Gagal mengupload!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        pd.cancel();
                        Toast.makeText(KRTActivity.this, "Gagal mengupload gambar! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadBarang2(View view) {
        String nama         = et_nama2.getText().toString();
        String keterangan   = et_keterangan2.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        String date = sdf.format(new Date());
        String filename = UUID.randomUUID().toString();

        Map<String, String> data = new HashMap<>();
        data.put("nama", nama);
        data.put("keterangan", keterangan);
        data.put("lokasi_gambar", filename);

        if(!nama.isEmpty() && !keterangan.isEmpty() && filePath2 != null){
            pd.show();

            StorageReference ref = storageReference.child("images/" + filename);
            ref.putFile(filePath2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mRef2.child(date).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    et_nama2.setText("");
                                    et_keterangan2.setText("");
                                    iv_preview2.setImageBitmap(null);
                                    iv_preview2.setBackground(getResources().getDrawable(R.drawable.ic_baseline_image_360));
                                    Toast.makeText(KRTActivity.this, "Berhasil mengupload!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(KRTActivity.this, "Gagal mengupload!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        pd.cancel();
                        Toast.makeText(KRTActivity.this, "Gagal mengupload gambar! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        if(requestCode == PICK_IMAGE_REQUEST1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath1 = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath1);
                iv_preview1.setImageBitmap(bitmap);
                iv_preview1.setBackgroundColor(Color.WHITE);
            }catch (IOException e){
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PICK_IMAGE_REQUEST2 && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath2 = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath2);
                iv_preview2.setImageBitmap(bitmap);
                iv_preview2.setBackgroundColor(Color.WHITE);
            }catch (IOException e){
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_LOGOUT && resultCode == RESULT_OK){
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(KRTActivity.this, MainActivity.class));
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
                startActivityForResult(new Intent(KRTActivity.this, AccountActivity.class), REQUEST_LOGOUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}