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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.manajemenorganisasi.adapters.MahasiswaAdapter;
import com.app.manajemenorganisasi.interfaces.MahasiswaInterface;
import com.app.manajemenorganisasi.models.Mahasiswa;
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

public class DataMahasiswaActivity extends AppCompatActivity {

    private RecyclerView rv_mahasiswa;
    private List<Mahasiswa> mahasiswaList;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ProgressDialog pd;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabel_lihat_mahasiswa);

        rv_mahasiswa = findViewById(R.id.rv_mahasiswa);

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses..");

        mDatabase = FirebaseDatabase.getInstance();

        firebaseStorage     = FirebaseStorage.getInstance();
        storageReference    = firebaseStorage.getReference();

        mRef = mDatabase.getReference("mahasiswa");

        mahasiswaList = new ArrayList<>();
        MahasiswaAdapter mahasiswaAdapter = new MahasiswaAdapter(mahasiswaList, this);
        mahasiswaAdapter.setMahasiswaInterface(new MahasiswaInterface() {
            @Override
            public void onDelete(Mahasiswa mahasiswa) {
                AlertDialog.Builder adb = new AlertDialog.Builder(DataMahasiswaActivity.this);
                adb.setMessage("Apakah anda yakin ingin menghapus data "+ mahasiswa.getNama() + "?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        mRef.child(mahasiswa.getTanggal_ditambahkan()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(DataMahasiswaActivity.this, "Berhasil menghapus!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(DataMahasiswaActivity.this, "Gagal menghapus!", Toast.LENGTH_SHORT).show();
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

            @Override
            public void onDownload(Mahasiswa mahasiswa) {
                pd.setMessage("Downloading " + mahasiswa.getLokasi_file() + " ..");
                pd.show();
                StorageReference ref = storageReference.child("documents/" + mahasiswa.getLokasi_file());
                try {
                    File localFile = File.createTempFile("data_mahasiswa", "");
                    ref.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                            pd.cancel();
                            if(task.isSuccessful()){

                                Uri fileuri = FileProvider.getUriForFile(DataMahasiswaActivity.this, getPackageName() + ".provider", localFile);
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(fileuri,"application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Intent intent = Intent.createChooser(target, "Open File");
                                try {
                                    startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(DataMahasiswaActivity.this, "Aplikasi tidak ditemukan, pastikan terdapat aplikas pembuka dokumen!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(DataMahasiswaActivity.this, "Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    pd.cancel();
                    Toast.makeText(DataMahasiswaActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        rv_mahasiswa.setLayoutManager(new LinearLayoutManager(this));
        rv_mahasiswa.setAdapter(mahasiswaAdapter);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mahasiswaList.clear();
                mahasiswaList.add(new Mahasiswa());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Mahasiswa mahasiswa = snapshot1.getValue(Mahasiswa.class);
                    mahasiswaList.add(mahasiswa);
                }
                mahasiswaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void mahasiswa_download_pdf(View view) {
        createPdf(this, findViewById(R.id.mahasiswa_container));
    }
}