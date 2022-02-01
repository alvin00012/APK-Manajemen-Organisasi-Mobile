package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.app.manajemenorganisasi.models.Kegiatan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class SDMActivity extends AppCompatActivity {

    private EditText et_nama, et_deskripsi, et_jadwal;
    private ProgressDialog pd;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private SharedPreferences sharedPreferences;
    private static final int REQUEST_LOGOUT = 5001;
    private List<Kegiatan> kegiatanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdm);
        et_nama         = findViewById(R.id.et_nama_kegiatan);
        et_deskripsi    = findViewById(R.id.et_deskripsi_kegiatan);
        et_jadwal       = findViewById(R.id.et_jadwal_kegiatan);

        pd              = new ProgressDialog(this);
        pd.setMessage("Menyiapkan..");

        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        mDatabase = FirebaseDatabase.getInstance();

        kegiatanList = new ArrayList<>();

        /*
        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group = sharedPreferences.getString("role_group", "");
            mRef = mDatabase.getReference(group + "/kegiatan");
        }else{
            startActivity(new Intent(sdm.this, MainActivity.class));
            finish();
        }
         */

        mRef = mDatabase.getReference("kegiatan");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                kegiatanList.clear();
                for (DataSnapshot snapshot1:snapshot.getChildren()) {
                    kegiatanList.add(snapshot1.getValue(Kegiatan.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void jadwal(View view){
        if(!et_nama.getText().toString().isEmpty() && !et_deskripsi.getText().toString().isEmpty() && !et_jadwal.getText().toString().isEmpty()){
            pd.setMessage("Memproses..");
            pd.show();
            Map<String, String> data = new HashMap<>();
            data.put("nama", et_nama.getText().toString());
            data.put("deskripsi", et_deskripsi.getText().toString());
            data.put("tanggal", et_jadwal.getText().toString());
            String key = mRef.push().getKey();
            mRef.child(key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.cancel();
                    if(task.isSuccessful()){
                        Toast.makeText(SDMActivity.this, "Berhasil membuat kegiatan!", Toast.LENGTH_SHORT).show();
                        et_nama.setText("");
                        et_deskripsi.setText("");
                        et_jadwal.setText("");
                    }else{
                        Toast.makeText(SDMActivity.this, "Gagal membuat kegiatan!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(SDMActivity.this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getJadwal(View view) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Launch Time Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                boolean hasTaken = false;
                for (Kegiatan kegiatan:kegiatanList) {
                    Date date = kegiatan.getTanggalInDate();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int dom = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    if(dom == i2 && month == i1 && year == i){
                        hasTaken = true;
                    }
                }
                if(!hasTaken){
                    et_jadwal.setText(String.format("%02d/%02d/%04d", datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear()));
                }else{
                    Toast.makeText(SDMActivity.this, "Jadwal telah terpakai!", Toast.LENGTH_SHORT).show();
                }
            }
        },year, month, day);
        dpd.show();
    }

    public void datamahasiwa(View view) {
        startActivity(new Intent(SDMActivity.this, DataMahasiswaActivity.class));
    }

    public void minat(View view) {
        startActivity(new Intent(SDMActivity.this, LihatMinatActivity.class));
    }

    public void tambahMahasiswa(View view) {
        startActivity(new Intent(SDMActivity.this, TambahMahasiswaActivity.class));
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
                startActivityForResult(new Intent(SDMActivity.this, AccountActivity.class), REQUEST_LOGOUT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_LOGOUT && resultCode == RESULT_OK){
            sharedPreferences.edit().clear().apply();
            startActivity(new Intent(SDMActivity.this, MainActivity.class));
        }
    }
}