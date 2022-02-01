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

public class MedinfoActivity extends AppCompatActivity {

    private static final int REQUEST_LOGOUT = 5001;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef_berita, mRef_organisasi;
    private EditText et_judul_berita, et_subjudul_berita, et_isi_berita, et_caption;
    private EditText et_pengertian_org, et_desk_org;
    private ProgressDialog pd;
    private ImageView iv_preview, iv_preview_org;

    private static final int REQUEST_PICK_IMAGE = 9000, REQUEST_PICK_IMAGE_ORG = 9001;
    private Uri filePath, filePathOrg;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medinfo);

        pd              = new ProgressDialog(this);
        pd.setMessage("Memproses..");
        et_judul_berita     = findViewById(R.id.et_judul_berita);
        et_isi_berita       = findViewById(R.id.et_isi_berita);
        et_subjudul_berita  = findViewById(R.id.et_subjudul_berita);
        et_caption          = findViewById(R.id.et_caption);

        et_pengertian_org    = findViewById(R.id.et_pengertian_organisasi);
        et_desk_org     = findViewById(R.id.et_deskripsi_organisasi);

        iv_preview      = findViewById(R.id.iv_preview);
        iv_preview_org  = findViewById(R.id.iv_preview_organisasi);

        mDatabase       = FirebaseDatabase.getInstance();

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group = sharedPreferences.getString("role_group", "");
            mRef_berita     = mDatabase.getReference(group + "/berita");
            mRef_organisasi = mDatabase.getReference(group + "/organisasi");
        }else{
            startActivity(new Intent(MedinfoActivity.this, MainActivity.class));
            finish();
        }

        firebaseStorage     = FirebaseStorage.getInstance();
        storageReference    = firebaseStorage.getReference();
    }

    public void pratinjau(View view) {
        String judul, subjudul, isi, caption;
        judul       = et_judul_berita.getText().toString();
        subjudul    = et_subjudul_berita.getText().toString();
        isi         = et_isi_berita.getText().toString();
        caption     = et_caption.getText().toString();

        if(!judul.isEmpty() && !subjudul.isEmpty() && !isi.isEmpty() && !caption.isEmpty() && filePath != null){
            Intent i = new Intent(MedinfoActivity.this, BeritaActivity.class);
            i.putExtra("judul", judul);
            i.putExtra("subjudul", subjudul);
            i.putExtra("isi", isi);
            i.putExtra("caption", caption);
            i.putExtra("gambar", filePath);
            i.putExtra("pratinjau", true);

            startActivity(i);
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    public void pratinjau2(View view) {
        String pengertian, tanggal, deskripsi;
        pengertian              = et_pengertian_org.getText().toString();
        SimpleDateFormat sdf    = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        tanggal                 = sdf.format(new Date());
        deskripsi               = et_desk_org.getText().toString();

        if(!pengertian.isEmpty() && !tanggal.isEmpty() && !deskripsi.isEmpty() && filePathOrg != null){
            Intent i = new Intent(MedinfoActivity.this, OrmawaActivity.class);
            i.putExtra("pengertian", pengertian);
            i.putExtra("tanggal", tanggal);
            i.putExtra("deskripsi", deskripsi);
            i.putExtra("gambar", filePathOrg);
            i.putExtra("pratinjau", true);
            startActivity(i);
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    public void tambahGambar(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Pilih Gambar"), REQUEST_PICK_IMAGE);
    }

    public void tambahGambarOrg(View view) {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Pilih Gambar"), REQUEST_PICK_IMAGE_ORG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv_preview.setImageBitmap(bitmap);
                iv_preview.setBackgroundColor(Color.WHITE);
            }catch (IOException e){
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_PICK_IMAGE_ORG && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePathOrg = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathOrg);
                iv_preview_org.setImageBitmap(bitmap);
                iv_preview_org.setBackgroundColor(Color.WHITE);
            }catch (IOException e){
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_LOGOUT && resultCode == RESULT_OK){
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(MedinfoActivity.this, MainActivity.class));
        }
    }

    public void uploadOrganisasi(View view) {
        if(!et_desk_org.getText().toString().isEmpty()){
            pd.show();
            String nama_file, deskripsi, pengertian;

            pengertian  = et_pengertian_org.getText().toString();
            deskripsi   = et_desk_org.getText().toString();
            nama_file   = "organisasi";

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
            String date = sdf.format(new Date());

            Map<String, String> data = new HashMap<>();
            data.put("deskripsi", deskripsi);
            data.put("pengertian", pengertian);
            data.put("tanggal", date);
            data.put("lokasi_gambar", nama_file);

            StorageReference ref = storageReference.child("images/" + nama_file);
            ref.putFile(filePathOrg).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mRef_organisasi.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(MedinfoActivity.this, "Berhasil mengupload!", Toast.LENGTH_SHORT).show();
                                    et_pengertian_org.setText("");
                                    et_desk_org.setText("");
                                    iv_preview_org.setImageBitmap(null);
                                    iv_preview_org.setBackground(getResources().getDrawable(R.drawable.ic_baseline_image_360));
                                }else{
                                    Toast.makeText(MedinfoActivity.this, "Gagal mengupload!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        pd.cancel();
                        Toast.makeText(MedinfoActivity.this, "Gagal mengupload gambar! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadBerita(View view) {
        String judul, subjudul, isi, caption, nama_file;
        judul       = et_judul_berita.getText().toString();
        subjudul    = et_subjudul_berita.getText().toString();
        isi         = et_isi_berita.getText().toString();
        caption     = et_caption.getText().toString();
        nama_file   = UUID.randomUUID().toString();

        if(!judul.isEmpty() && !subjudul.isEmpty() && !isi.isEmpty() && !caption.isEmpty()){
            pd.show();
            Map<String, String> data = new HashMap<>();
            data.put("judul", judul);
            data.put("subjudul", subjudul);
            data.put("isi", isi);
            data.put("caption", caption);
            data.put("lokasi_gambar", nama_file);

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
            String date = sdf.format(new Date());

            StorageReference ref = storageReference.child("images/" + nama_file);
            ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mRef_berita.child(date).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(MedinfoActivity.this, "Berhasil mengupload berita!", Toast.LENGTH_SHORT).show();
                                    et_judul_berita.setText("");
                                    et_subjudul_berita.setText("");
                                    et_isi_berita.setText("");
                                    et_caption.setText("");
                                    iv_preview.setImageBitmap(null);
                                    iv_preview.setBackground(getResources().getDrawable(R.drawable.ic_baseline_image_360));
                                }else{
                                    Toast.makeText(MedinfoActivity.this, "Gagal mengupload berita!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        pd.cancel();
                        Toast.makeText(MedinfoActivity.this, "Gagal mengupload gambar! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                startActivityForResult(new Intent(MedinfoActivity.this, AccountActivity.class), REQUEST_LOGOUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}