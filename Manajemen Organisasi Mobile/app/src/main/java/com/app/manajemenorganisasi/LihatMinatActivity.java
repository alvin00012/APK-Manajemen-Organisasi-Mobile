package com.app.manajemenorganisasi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.manajemenorganisasi.adapters.MinatBakatAdapter;
import com.app.manajemenorganisasi.interfaces.MinatBakatInterface;
import com.app.manajemenorganisasi.models.MinatBakat;
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

public class LihatMinatActivity extends AppCompatActivity {

    private RecyclerView rv_minat_bakat;
    private List<MinatBakat> minatBakatList;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private SharedPreferences sharedPreferences;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabel_lihat_minat);

        rv_minat_bakat = findViewById(R.id.rv_minat_bakat);

        pd = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("manajemenorganisasi", MODE_PRIVATE);

        /*
        if(!sharedPreferences.getString("role_group", "").equals("")){
            String group = sharedPreferences.getString("role_group", "");
            mRef = mDatabase.getReference(group + "/minat_bakat");
        }else{
            startActivity(new Intent(lihat_minat.this, MainActivity.class));
            finish();
        }
         */

        mRef = mDatabase.getReference( "minat_bakat");

        minatBakatList = new ArrayList<>();
        MinatBakatAdapter minatBakatAdapter = new MinatBakatAdapter(minatBakatList, this);
        minatBakatAdapter.setMinatBakatInterface(new MinatBakatInterface() {
            @Override
            public void onDelete(MinatBakat minatBakat) {
                AlertDialog.Builder adb = new AlertDialog.Builder(LihatMinatActivity.this);
                adb.setMessage("Apakah anda yakin ingin menghapus data "+ minatBakat.getNama() + "?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        mRef.child(minatBakat.getNim()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.cancel();
                                if(task.isSuccessful()){
                                    Toast.makeText(LihatMinatActivity.this, "Berhasil menghapus!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LihatMinatActivity.this, "Gagal menghapus!", Toast.LENGTH_SHORT).show();
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

        rv_minat_bakat.setLayoutManager(new LinearLayoutManager(this));
        rv_minat_bakat.setAdapter(minatBakatAdapter);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                minatBakatList.clear();
                minatBakatList.add(new MinatBakat());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    MinatBakat minatBakat = snapshot1.getValue(MinatBakat.class);
                    minatBakat.setNim(snapshot1.getKey());
                    minatBakatList.add(minatBakat);
                }
                minatBakatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void minat_bakat_download_pdf(View view) {
        createPdf(this, findViewById(R.id.minat_bakat_container));
    }
}