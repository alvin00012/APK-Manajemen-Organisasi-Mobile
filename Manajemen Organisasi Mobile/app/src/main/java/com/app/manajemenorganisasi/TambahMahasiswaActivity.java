package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TambahMahasiswaActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_FILE = 9001;
    private EditText et_judul, et_nomor;
    private Spinner sp_jurusan;
    private LinearLayout linearLayout;

    private Uri filePath;

    private ProgressDialog pd;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mRef;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_mahasiswa);

        et_judul        = findViewById(R.id.et_tambah_mahasiswa_judul);
        et_nomor        = findViewById(R.id.et_tambah_mahasiswa_nomor);
        linearLayout    = findViewById(R.id.layoutDataMhs);

        sp_jurusan  = findViewById(R.id.sp_jurusan);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mRef = firebaseDatabase.getReference("mahasiswa");

        firebaseStorage     = FirebaseStorage.getInstance();
        storageReference    = firebaseStorage.getReference();

        pd = new ProgressDialog(this);
        pd.setMessage("Memproses");
    }

    public void kirimDataMahasiswa (View view){
        String judul = et_judul.getText().toString();
        String nomor = et_nomor.getText().toString();
        String jurusan = String.valueOf(sp_jurusan.getSelectedItem());
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        String date = sdf.format(new Date());
        String filename = UUID.randomUUID().toString();

        if(!judul.isEmpty() && !nomor.isEmpty() && !jurusan.isEmpty() && filePath != null){
            pd.show();

            Map<String, String> data = new HashMap<>();
            data.put("nama", judul);
            data.put("nomor", nomor);
            data.put("jurusan", jurusan);
            data.put("lokasi_file", filename + ".pdf");
            data.put("tanggal_ditambahkan", date);

            StorageReference ref = storageReference.child("documents/" + filename + ".pdf" );
            ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        mRef.child(date).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(TambahMahasiswaActivity.this, "Berhasil menambahkan!", Toast.LENGTH_SHORT).show();
                                    et_judul.setText("");
                                    et_nomor.setText("");
                                    linearLayout.setBackgroundColor(Color.WHITE);
                                }else{
                                    Toast.makeText(TambahMahasiswaActivity.this, "Gagal menambahkan!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(TambahMahasiswaActivity.this, "Gagal Upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    public void tambahFileDataMahasiswa(View view) {
        Intent i = new Intent();
        i.setType("application/pdf");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Pilih File"), REQUEST_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            linearLayout.setBackgroundColor(Color.GREEN);
        }
    }
}