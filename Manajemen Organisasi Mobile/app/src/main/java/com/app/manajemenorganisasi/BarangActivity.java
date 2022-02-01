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

import com.app.manajemenorganisasi.adapters.BarangAdapter;
import com.app.manajemenorganisasi.interfaces.BarangInterface;
import com.app.manajemenorganisasi.models.Barang;
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

public class BarangActivity extends AppCompatActivity {

    private RecyclerView rv_barang_masuk, rv_barang_keluar;
    private List<Barang> barangMasukList, barangKeluarList;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private SharedPreferences sharedPreferences;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_barang);

        rv_barang_masuk  = findViewById(R.id.rv_barang_masuk);
        rv_barang_keluar = findViewById(R.id.rv_barang_keluar);

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses..");

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group = sharedPreferences.getString("role_group", "");
            mRef = mDatabase.getReference(group);
        }else{
            startActivity(new Intent(BarangActivity.this, MainActivity.class));
            finish();
        }

        barangMasukList = new ArrayList<>();
        barangKeluarList = new ArrayList<>();

        BarangAdapter barangMasukAdapter = new BarangAdapter(barangMasukList, this);
        barangMasukAdapter.setBarangInterface(new BarangInterface() {
            @Override
            public void onDelete(Barang barang) {
                AlertDialog.Builder adb = new AlertDialog.Builder(BarangActivity.this);
                adb.setMessage("Apakah anda yakin ingin menghapus "+ barang.getNama() + "?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        mRef.child("barang_masuk").child(barang.getTanggal()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(BarangActivity.this, "Berhasil menghapus!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(BarangActivity.this, "Gagal menghapus!", Toast.LENGTH_SHORT).show();
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
        BarangAdapter barangKeluarAdapter = new BarangAdapter(barangKeluarList, this);
        barangKeluarAdapter.setBarangInterface(new BarangInterface() {
            @Override
            public void onDelete(Barang barang) {
                AlertDialog.Builder adb = new AlertDialog.Builder(BarangActivity.this);
                adb.setMessage("Apakah anda yakin ingin menghapus "+ barang.getNama() + "?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        mRef.child("barang_barang").child(barang.getTanggal()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(BarangActivity.this, "Berhasil menghapus!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(BarangActivity.this, "Gagal menghapus!", Toast.LENGTH_SHORT).show();
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
        rv_barang_masuk.setLayoutManager(new LinearLayoutManager(this));
        rv_barang_masuk.setAdapter(barangMasukAdapter);

        rv_barang_keluar.setLayoutManager(new LinearLayoutManager(this));
        rv_barang_keluar.setAdapter(barangKeluarAdapter);

        mRef.child("barang_masuk").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barangMasukList.clear();
                barangMasukList.add(new Barang());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Barang barang = snapshot1.getValue(Barang.class);
                    barang.setTanggal(snapshot1.getKey());
                    barangMasukList.add(barang);
                }
                barangMasukAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRef.child("barang_keluar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barangKeluarList.clear();
                barangKeluarList.add(new Barang());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Barang barang = snapshot1.getValue(Barang.class);
                    barang.setTanggal(snapshot1.getKey());
                    barangKeluarList.add(barang);
                }
                barangKeluarAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void barang_masuk_download_pdf(View view) {
        createPdf(this, findViewById(R.id.barang_masuk_container));
    }

    public void barang_keluar_download_pdf(View view) {
        createPdf(this, findViewById(R.id.barang_keluar_container));
    }
}