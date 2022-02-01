package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.manajemenorganisasi.adapters.UangAdapter;
import com.app.manajemenorganisasi.interfaces.UangInterface;
import com.app.manajemenorganisasi.models.Uang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.app.manajemenorganisasi.utils.PDFUtil.createPdf;

public class KeuanganActivity extends AppCompatActivity {

    private RecyclerView rv_uang_masuk, rv_uang_keluar;
    private List<Uang> uangMasukList, uangKeluarList;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private SharedPreferences sharedPreferences;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_keuangan);

        rv_uang_masuk  = findViewById(R.id.rv_uang_masuk);
        rv_uang_keluar = findViewById(R.id.rv_uang_keluar);

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses..");

        mDatabase = FirebaseDatabase.getInstance();

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group = sharedPreferences.getString("role_group", "");
            mRef = mDatabase.getReference(group);
        }else{
            startActivity(new Intent(KeuanganActivity.this, MainActivity.class));
            finish();
        }


        uangMasukList = new ArrayList<>();
        uangKeluarList = new ArrayList<>();

        UangAdapter uangMasukAdapter = new UangAdapter(uangMasukList, this);
        uangMasukAdapter.setUangInterface(new UangInterface() {
            @Override
            public void onDelete(Uang uang) {
                AlertDialog.Builder adb = new AlertDialog.Builder(KeuanganActivity.this);
                adb.setMessage("Apakah anda yakin ingin menghapus "+ uang.getKeterangan() + "?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        mRef.child("uang_masuk").child(uang.getTimestamp()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(KeuanganActivity.this, "Berhasil menghapus!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(KeuanganActivity.this, "Gagal menghapus!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                adb.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                adb.show();
            }
        });

        UangAdapter uangKeluarAdapter = new UangAdapter(uangKeluarList, this);
        uangKeluarAdapter.setUangInterface(new UangInterface() {
            @Override
            public void onDelete(Uang uang) {
                AlertDialog.Builder adb = new AlertDialog.Builder(KeuanganActivity.this);
                adb.setMessage("Apakah anda yakin ingin menghapus "+ uang.getKeterangan() + "?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        mRef.child("uang_keluar").child(uang.getTimestamp()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(KeuanganActivity.this, "Berhasil menghapus!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(KeuanganActivity.this, "Gagal menghapus!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                adb.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                adb.show();
            }
        });

        rv_uang_masuk.setLayoutManager(new LinearLayoutManager(this));
        rv_uang_masuk.setAdapter(uangMasukAdapter);

        rv_uang_keluar.setLayoutManager(new LinearLayoutManager(this));
        rv_uang_keluar.setAdapter(uangKeluarAdapter);

        mRef.child("uang_masuk").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uangMasukList.clear();
                uangMasukList.add(new Uang());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Uang uang = snapshot1.getValue(Uang.class);
                    uang.setTimestamp(snapshot1.getKey());
                    uangMasukList.add(uang);
                }
                uangMasukAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRef.child("uang_keluar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uangKeluarList.clear();
                uangKeluarList.add(new Uang());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Uang uang = snapshot1.getValue(Uang.class);
                    uang.setTimestamp(snapshot1.getKey());
                    uangKeluarList.add(uang);
                }
                uangKeluarAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void uang_masuk_download_pdf(View view) {
        createPdf(this, findViewById(R.id.uang_masuk_container));
    }

    public void uang_keluar_download_pdf(View view) {
        createPdf(this, findViewById(R.id.uang_keluar_container));
    }
}