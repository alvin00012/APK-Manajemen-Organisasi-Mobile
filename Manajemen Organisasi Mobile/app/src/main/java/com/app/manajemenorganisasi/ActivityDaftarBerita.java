package com.app.manajemenorganisasi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.manajemenorganisasi.adapters.BeritaAdapter;
import com.app.manajemenorganisasi.interfaces.BeritaInterface;
import com.app.manajemenorganisasi.models.Berita;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityDaftarBerita extends AppCompatActivity {

    private RecyclerView rv_berita;
    private List<Berita> beritaList;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_berita);

        rv_berita = findViewById(R.id.rv_berita);

        mDatabase = FirebaseDatabase.getInstance();

        if(getIntent().hasExtra("group")){
            String group = getIntent().getStringExtra("group");
            mRef = mDatabase.getReference(group + "/berita");
        }else{
            startActivity(new Intent(ActivityDaftarBerita.this, MainActivity.class));
            finish();
        }

        beritaList = new ArrayList<>();
        BeritaAdapter beritaAdapter = new BeritaAdapter(beritaList, this);
        beritaAdapter.setBeritaInterface(new BeritaInterface() {
            @Override
            public void onClick(Berita berita) {
                String judul, subjudul, isi, caption;
                judul       = berita.getJudul();
                subjudul    = berita.getSubjudul();
                isi         = berita.getIsi();
                caption     = berita.getCaption();

                Intent i = new Intent(ActivityDaftarBerita.this, BeritaActivity.class);
                i.putExtra("judul", judul);
                i.putExtra("subjudul", subjudul);
                i.putExtra("isi", isi);
                i.putExtra("caption", caption);
                i.putExtra("gambar", berita.getLokasi_gambar());
                i.putExtra("pratinjau", false);
                startActivity(i);
            }
        });

        rv_berita.setLayoutManager(new LinearLayoutManager(this));
        rv_berita.setAdapter(beritaAdapter);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Berita berita = dataSnapshot1.getValue(Berita.class);
                    berita.setDipublikasikan(dataSnapshot1.getKey());
                    beritaList.add(berita);
                }
                if(beritaList.size() == 0){
                    findViewById(R.id.tv_belum_ada_berita).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.tv_belum_ada_berita).setVisibility(View.GONE);
                }
                beritaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("PIONATE", "Failed to read value.", error.toException());
            }
        });
    }
}