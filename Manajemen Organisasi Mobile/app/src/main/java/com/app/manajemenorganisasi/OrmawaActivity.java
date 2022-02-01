package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.manajemenorganisasi.models.Organisasi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.app.manajemenorganisasi.utils.DateUtil.dow_name;
import static com.app.manajemenorganisasi.utils.DateUtil.month_name;

public class OrmawaActivity extends AppCompatActivity {

    private TextView tv_pengertian, tv_tanggal, tv_deskripsi;
    private ImageView iv_gambar;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omawa);

        tv_pengertian   = findViewById(R.id.tv_organisasi_pengertian);
        tv_tanggal      = findViewById(R.id.tv_organisasi_tanggal);
        tv_deskripsi    = findViewById(R.id.tv_organisasi_deskripsi);

        iv_gambar       = findViewById(R.id.iv_organisasi_gambar);
        shimmerFrameLayout  = findViewById(R.id.shimmer_layout_organisasi);

        mDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        if(getIntent().hasExtra("pratinjau")){
            hideLoading();

            String pengertian, tanggal, deskripsi;
            Uri filePathOrg = getIntent().getParcelableExtra("gambar");
            pengertian = getIntent().getStringExtra("pengertian");
            tanggal = getIntent().getStringExtra("tanggal");
            deskripsi = getIntent().getStringExtra("deskripsi");

            tv_pengertian.setText(pengertian);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
            try {
                Date date = sdf.parse(tanggal);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                String dow = dow_name[calendar.get(Calendar.DAY_OF_WEEK) - 1];
                int dom = calendar.get(Calendar.DAY_OF_MONTH);

                String month = month_name[calendar.get(Calendar.MONTH) - 1];
                int year = calendar.get(Calendar.YEAR);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                tv_tanggal.setText(String.format("%s, %02d %s %04d %02d:%02d", dow, dom, month, year, hour, minute));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tv_deskripsi.setText(deskripsi);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathOrg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            iv_gambar.setImageBitmap(bitmap);
            iv_gambar.setBackgroundColor(Color.WHITE);
            findViewById(R.id.tv_data_tidak_tersedia).setVisibility(View.GONE);
        }else{
            if(getIntent().hasExtra("group")){
                String group = getIntent().getStringExtra("group");
                mRef = mDatabase.getReference(group + "/organisasi");
            }else{
                startActivity(new Intent(OrmawaActivity.this, MainActivity.class));
                finish();
            }

            showLoading();
            mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Organisasi organisasi = snapshot.getValue(Organisasi.class);

                    if(organisasi != null){
                        findViewById(R.id.tv_data_tidak_tersedia).setVisibility(View.GONE);
                        tv_pengertian.setText(organisasi.getPengertian());

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
                        try {
                            Date date = sdf.parse(organisasi.getTanggal());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);

                            String dow = dow_name[calendar.get(Calendar.DAY_OF_WEEK) - 1];
                            int dom = calendar.get(Calendar.DAY_OF_MONTH);

                            String month = month_name[calendar.get(Calendar.MONTH) - 1];
                            int year = calendar.get(Calendar.YEAR);
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);
                            tv_tanggal.setText(String.format("%s, %02d %s %04d %02d:%02d", dow, dom, month, year, hour, minute));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        tv_deskripsi.setText(organisasi.getDeskripsi());

                        storageReference.child("images/" + organisasi.getLokasi_gambar()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(OrmawaActivity.this)
                                        .load(uri)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                hideLoading();
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                hideLoading();
                                                return false;
                                            }
                                        })
                                        .into(iv_gambar);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                hideLoading();
                            }
                        });
                    }else{
                        hideLoading();
                        findViewById(R.id.tv_data_tidak_tersedia).setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hideLoading();
                }
            });
        }
    }

    private void showLoading(){
        shimmerFrameLayout.startShimmerAnimation();
        findViewById(R.id.view1).setVisibility(View.VISIBLE);
        findViewById(R.id.view2).setVisibility(View.VISIBLE);
        findViewById(R.id.view3).setVisibility(View.VISIBLE);
        findViewById(R.id.view4).setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        findViewById(R.id.view1).setVisibility(View.GONE);
        findViewById(R.id.view2).setVisibility(View.GONE);
        findViewById(R.id.view3).setVisibility(View.GONE);
        findViewById(R.id.view4).setVisibility(View.GONE);
        shimmerFrameLayout.stopShimmerAnimation();
    }
}