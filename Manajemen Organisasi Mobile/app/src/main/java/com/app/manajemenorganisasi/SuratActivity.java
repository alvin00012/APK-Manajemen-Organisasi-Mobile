package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.manajemenorganisasi.adapters.SuratAdapter;
import com.app.manajemenorganisasi.interfaces.SuratInterface;
import com.app.manajemenorganisasi.models.Surat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.app.manajemenorganisasi.utils.PDFUtil.createPdf;

public class SuratActivity extends AppCompatActivity {
    private RecyclerView rv_surat_masuk, rv_surat_keluar;
    private List<Surat> suratMasukList, suratKeluarList;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private SharedPreferences sharedPreferences;
    private ProgressDialog pd;
    private int totalProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_surat);

        rv_surat_masuk  = findViewById(R.id.rv_surat_masuk);
        rv_surat_keluar = findViewById(R.id.rv_surat_keluar);

        pd = new ProgressDialog(this);
        pd.setMessage("Menyiapkan..");

        mDatabase = FirebaseDatabase.getInstance();
        firebaseStorage     = FirebaseStorage.getInstance();
        storageReference    = firebaseStorage.getReference();

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group = sharedPreferences.getString("role_group", "");
            mRef = mDatabase.getReference(group);
        }else{
            startActivity(new Intent(SuratActivity.this, MainActivity.class));
            finish();
        }

        suratMasukList = new ArrayList<>();
        suratKeluarList = new ArrayList<>();

        SuratAdapter suratMasukAdapter = new SuratAdapter(suratMasukList, this);
        suratMasukAdapter.setSuratInterface(new SuratInterface() {
            @Override
            public void onDownload(Surat surat) {
                pd.setMessage("Downloading " + surat.getLokasi_surat() + " ..");
                pd.show();
                StorageReference ref = storageReference.child("documents/" + surat.getLokasi_surat());
                try {
                    File localFile = File.createTempFile("surat_masuk", "");
                    ref.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            pd.cancel();
                            if(task.isSuccessful()){

                                Uri fileuri = FileProvider.getUriForFile(SuratActivity.this, getPackageName() + ".provider", localFile);
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(fileuri,"application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    //Toast.makeText(data_surat.this, "Open File", Toast.LENGTH_SHORT).show();
                                    Log.i("PIONED", "sini");
                                    startActivity(intent);
                                    Log.i("PIONED", "sini");
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(SuratActivity.this, "Aplikasi tidak ditemukan, pastikan terdapat aplikas pembuka dokumen!", Toast.LENGTH_SHORT).show();
                                    // Instruct the user to install a PDF reader here, or something
                                }
                            }else{
                                Toast.makeText(SuratActivity.this, "Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    pd.cancel();
                    Toast.makeText(SuratActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onDelete(Surat surat) {
                AlertDialog.Builder adb = new AlertDialog.Builder(SuratActivity.this);
                adb.setMessage("Apakah anda yakin ingin menghapus "+ surat.getJudul() + "?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        mRef.child("surat_masuk").child(surat.getDatetime()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(SuratActivity.this, "Berhasil menghapus!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(SuratActivity.this, "Gagal menghapus!", Toast.LENGTH_SHORT).show();
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

        SuratAdapter suratKeluarAdapter = new SuratAdapter(suratKeluarList, this);
        suratKeluarAdapter.setSuratInterface(new SuratInterface() {
            @Override
            public void onDownload(Surat surat) {
                pd.setMessage("Downloading " + surat.getLokasi_surat() + " ..");
                pd.show();
                StorageReference ref = storageReference.child("documents/" + surat.getLokasi_surat());
                try {
                    File localFile = File.createTempFile("surat_keluar", "");
                    ref.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            pd.cancel();
                            if(task.isSuccessful()){

                                Uri fileuri = FileProvider.getUriForFile(SuratActivity.this, getPackageName() + ".provider", localFile);
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(fileuri,"application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(SuratActivity.this, "Aplikasi tidak ditemukan, pastikan terdapat aplikas pembuka dokumen!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(SuratActivity.this, "Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    pd.cancel();
                    Toast.makeText(SuratActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onDelete(Surat surat) {
                AlertDialog.Builder adb = new AlertDialog.Builder(SuratActivity.this);
                adb.setMessage("Apakah anda yakin ingin menghapus "+ surat.getJudul() + "?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        mRef.child("surat_keluar").child(surat.getDatetime()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(SuratActivity.this, "Berhasil menghapus!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(SuratActivity.this, "Gagal menghapus!", Toast.LENGTH_SHORT).show();
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

        rv_surat_masuk.setLayoutManager(new LinearLayoutManager(this));
        rv_surat_masuk.setAdapter(suratMasukAdapter);

        rv_surat_keluar.setLayoutManager(new LinearLayoutManager(this));
        rv_surat_keluar.setAdapter(suratKeluarAdapter);

        pd.show();

        mRef.child("surat_masuk").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkProgress();
                suratMasukList.clear();
                suratMasukList.add(new Surat());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Surat surat = snapshot1.getValue(Surat.class);
                    surat.setDatetime(snapshot1.getKey());
                    suratMasukList.add(surat);
                }
                suratMasukAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkProgress();
            }
        });

        mRef.child("surat_keluar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                checkProgress();
                suratKeluarList.clear();
                suratKeluarList.add(new Surat());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Surat surat = snapshot1.getValue(Surat.class);
                    surat.setDatetime(snapshot1.getKey());
                    suratKeluarList.add(surat);
                }
                suratKeluarAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                checkProgress();
            }
        });
    }

    public void surat_masuk_download_pdf(View view) {
        createPdf(this, findViewById(R.id.surat_masuk_container));
    }

    public void surat_keluar_download_pdf(View view) {
        createPdf(this, findViewById(R.id.surat_keluar_container));
    }

    private void checkProgress(){
        if(totalProgress >= 1){
            pd.cancel();
        }else{
            totalProgress++;
        }

        Log.i("PIONED", "Total: " + totalProgress);
    }
}