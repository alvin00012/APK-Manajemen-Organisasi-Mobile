package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class BeritaActivity extends AppCompatActivity {

    private TextView tv_judul, tv_subjudul, tv_isi, tv_caption;
    private ImageView iv_gambar;
    private Uri gambar;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berita);

        tv_judul    = findViewById(R.id.tv_judul);
        tv_subjudul = findViewById(R.id.tv_subjudul);
        tv_isi      = findViewById(R.id.tv_isi_berita);
        tv_caption  = findViewById(R.id.tv_caption);
        iv_gambar   = findViewById(R.id.iv_gambar);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        if(getIntent().hasExtra("judul")
                && getIntent().hasExtra("subjudul")
                && getIntent().hasExtra("isi")
                && getIntent().hasExtra("caption")
                && getIntent().hasExtra("pratinjau")
                && getIntent().hasExtra("gambar")){

            String judul = getIntent().getStringExtra("judul");
            String subjudul = getIntent().getStringExtra("subjudul");
            String isi = getIntent().getStringExtra("isi");
            String caption = getIntent().getStringExtra("caption");

            tv_judul.setText(judul);
            tv_subjudul.setText(subjudul);
            tv_isi.setText(isi);
            tv_caption.setText(caption);

            boolean pratinjau = getIntent().getBooleanExtra("pratinjau", false);
            if(pratinjau){
                Uri filePath = getIntent().getParcelableExtra("gambar");
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iv_gambar.setImageBitmap(bitmap);
            }else{
                String gambar = getIntent().getStringExtra("gambar");
                storageReference.child("images/" + gambar).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(BeritaActivity.this)
                                .load(uri)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        iv_gambar.setVisibility(View.VISIBLE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        iv_gambar.setVisibility(View.VISIBLE);
                                        return false;
                                    }
                                })
                                .into(iv_gambar);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }

        }
    }
}