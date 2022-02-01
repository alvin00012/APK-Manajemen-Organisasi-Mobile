package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MinatNBakatActivity extends AppCompatActivity {

    private Spinner sp_kesenian, sp_olahraga, sp_keteknikan, sp_jurusan;
    private EditText et_kesenian_lainnya, et_olahraga_lainnya, et_keteknikan_lainnya, et_nama, et_nim, et_alasan;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minat_bakat);

        sp_kesenian     = findViewById(R.id.sp_kesenian);
        sp_olahraga     = findViewById(R.id.sp_olahraga);
        sp_keteknikan   = findViewById(R.id.sp_keteknikan);

        et_kesenian_lainnya     = findViewById(R.id.et_kesenian_lainnya);
        et_olahraga_lainnya     = findViewById(R.id.et_olahraga_lainnya);
        et_keteknikan_lainnya   = findViewById(R.id.et_keteknikan_lainnya);

        et_nama                 = findViewById(R.id.et_nama);
        sp_jurusan              = findViewById(R.id.sp_jurusan);
        et_nim                  = findViewById(R.id.et_nim);
        et_alasan               = findViewById(R.id.et_alasan);

        pd                      = new ProgressDialog(this);
        pd.setMessage("Memproses..");

        mDatabase = FirebaseDatabase.getInstance();

        /*
        if(getIntent().hasExtra("group")){
            String group = getIntent().getStringExtra("group");
            mRef = mDatabase.getReference(group + "/minat_bakat");
        }else{
            startActivity(new Intent(minat_bakat.this, MainActivity.class));
            finish();
        }
        */

        mRef = mDatabase.getReference("minat_bakat");

        sp_kesenian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(String.valueOf(sp_kesenian.getSelectedItem()).equals("Lainnya")){
                    et_kesenian_lainnya.setVisibility(View.VISIBLE);
                }else{
                    et_kesenian_lainnya.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_olahraga.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(String.valueOf(sp_olahraga.getSelectedItem()).equals("Lainnya")){
                    et_olahraga_lainnya.setVisibility(View.VISIBLE);
                }else{
                    et_olahraga_lainnya.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_keteknikan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(String.valueOf(sp_keteknikan.getSelectedItem()).equals("Lainnya")){
                    et_keteknikan_lainnya.setVisibility(View.VISIBLE);
                }else{
                    et_keteknikan_lainnya.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void kirim(View view){
        String nama, jurusan, nim, kesenian = "", olahraga = "", keteknikan = "", alasan;

        nama = et_nama.getText().toString();
        jurusan = String.valueOf(sp_jurusan.getSelectedItem());
        nim = et_nim.getText().toString();

        if(et_kesenian_lainnya.getVisibility() == View.VISIBLE){
            kesenian = et_kesenian_lainnya.getText().toString();
        }else{
            kesenian = String.valueOf(sp_kesenian.getSelectedItem());
        }

        if(et_olahraga_lainnya.getVisibility() == View.VISIBLE){
            olahraga = et_olahraga_lainnya.getText().toString();
        }else{
            olahraga = String.valueOf(sp_olahraga.getSelectedItem());
        }

        if(et_keteknikan_lainnya.getVisibility() == View.VISIBLE){
            keteknikan = et_keteknikan_lainnya.getText().toString();
        }else{
            keteknikan = String.valueOf(sp_keteknikan.getSelectedItem());
        }

        alasan = et_alasan.getText().toString();

        if(!nama.isEmpty() && !jurusan.isEmpty() && !nim.isEmpty() && kesenian != "" && olahraga != "" && keteknikan != "" && !alasan.isEmpty()){
            Toast.makeText(this, "Data terkirim!", Toast.LENGTH_SHORT).show();
            pd.show();

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
            String date = sdf.format(new Date());

            Map<String, String> data = new HashMap<>();
            data.put("nama", nama);
            data.put("jurusan", jurusan);
            data.put("kesenian", kesenian);
            data.put("olahraga", olahraga);
            data.put("keteknikan", keteknikan);
            data.put("alasan", alasan);
            data.put("timestamp", date);

            mRef.child(nim).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.cancel();
                    if(task.isSuccessful()){
                        Toast.makeText(MinatNBakatActivity.this, "Data terkirim!", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(MinatNBakatActivity.this, "Gagal mengirim data!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "Data belum lengkap!", Toast.LENGTH_SHORT).show();
        }
    }
}