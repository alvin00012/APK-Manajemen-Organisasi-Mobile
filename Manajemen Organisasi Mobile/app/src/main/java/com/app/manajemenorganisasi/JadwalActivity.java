package com.app.manajemenorganisasi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.app.manajemenorganisasi.utils.DateUtil.dow_name;
import static com.app.manajemenorganisasi.utils.DateUtil.month_name;

public class JadwalActivity extends AppCompatActivity {

    private TextView tv_jadwal, tv_nama, tv_deskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_omawa);

        tv_jadwal       = findViewById(R.id.tv_kegiatan_tanggal);
        tv_nama         = findViewById(R.id.tv_kegiatan_nama);
        tv_deskripsi    = findViewById(R.id.tv_kegiatan_deskripsi);

        if(getIntent().hasExtra("deskripsi") && getIntent().hasExtra("nama") && getIntent().hasExtra("tanggal")){
            String nama         = getIntent().getStringExtra("nama");
            String deskripsi    = getIntent().getStringExtra("deskripsi");
            String tanggal      = getIntent().getStringExtra("tanggal");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = sdf.parse(tanggal);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                String dow = dow_name[calendar.get(Calendar.DAY_OF_WEEK) - 1];
                int dom = calendar.get(Calendar.DAY_OF_MONTH);


                String month = month_name[calendar.get(Calendar.MONTH) - 1];
                int year = calendar.get(Calendar.YEAR);

                tv_jadwal.setText(String.format("%s, %02d %s %04d", dow, dom, month, year));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tv_nama.setText(String.format("Diberitahukan untuk seluruh mahasiswa bahwasanya akan dilaksanakan kegiatan %s pada tanggal sbb:", nama));
            tv_deskripsi.setText(deskripsi);
        }


    }
}